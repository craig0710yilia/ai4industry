<template>
  <div>
    <el-card style="margin-bottom: 16px;">
      <div style="display: flex; align-items: center; gap: 12px;">
        <el-button @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon> Back
        </el-button>
        <div>
          <span style="font-size: 14px; color: #666;">Data Source: </span>
          <span style="font-size: 16px; font-weight: bold;">{{ dataSourceName }}</span>
          <el-tag v-if="dataSourceProtocol" :type="protocolTagType(dataSourceProtocol)" style="margin-left: 8px;">
            {{ dataSourceProtocol }}
          </el-tag>
        </div>
      </div>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 16px; font-weight: bold;">Data Points</span>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><Plus /></el-icon> Add Data Point
          </el-button>
        </div>
      </template>

      <el-table :data="dataPoints" v-loading="loading" stripe border>
        <el-table-column prop="tagName" label="Tag Name" min-width="150" />
        <el-table-column prop="displayName" label="Display Name" min-width="150" />
        <el-table-column prop="dataType" label="Data Type" width="110">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.dataType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Address Info" min-width="180">
          <template #default="{ row }">
            <span v-if="dataSourceProtocol === 'OPCUA'">{{ row.opcuaNodeId }}</span>
            <span v-else-if="dataSourceProtocol === 'MODBUS_TCP'">
              {{ row.modbusRegisterType }} @ {{ row.modbusRegisterAddress }}
            </span>
            <span v-else-if="dataSourceProtocol === 'MQTT'">{{ row.mqttTopic }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sampleIntervalMs" label="Interval (ms)" width="120" />
        <el-table-column prop="enabled" label="Enabled" width="90">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="small">
              {{ row.enabled ? 'Yes' : 'No' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEditDialog(row)">Edit</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Data Point' : 'Add Data Point'" width="580px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="160px">
        <el-form-item label="Tag Name" prop="tagName">
          <el-input v-model="form.tagName" placeholder="Unique tag name" />
        </el-form-item>
        <el-form-item label="Display Name">
          <el-input v-model="form.displayName" />
        </el-form-item>
        <el-form-item label="Data Type" prop="dataType">
          <el-select v-model="form.dataType" style="width: 100%;">
            <el-option v-for="t in dataTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="Sample Interval (ms)">
          <el-input-number v-model="form.sampleIntervalMs" :min="100" :max="3600000" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="Enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>

        <!-- OPC-UA fields -->
        <template v-if="dataSourceProtocol === 'OPCUA'">
          <el-divider content-position="left">OPC-UA</el-divider>
          <el-form-item label="Node ID">
            <el-input v-model="form.opcuaNodeId" placeholder="ns=2;i=1001" />
          </el-form-item>
        </template>

        <!-- Modbus TCP fields -->
        <template v-if="dataSourceProtocol === 'MODBUS_TCP'">
          <el-divider content-position="left">Modbus TCP</el-divider>
          <el-form-item label="Register Type">
            <el-select v-model="form.modbusRegisterType" style="width: 100%;">
              <el-option label="Coil" value="COIL" />
              <el-option label="Discrete Input" value="DISCRETE" />
              <el-option label="Input Register" value="INPUT_REGISTER" />
              <el-option label="Holding Register" value="HOLDING_REGISTER" />
            </el-select>
          </el-form-item>
          <el-form-item label="Register Address">
            <el-input-number v-model="form.modbusRegisterAddress" :min="0" :max="65535" style="width: 100%;" />
          </el-form-item>
        </template>

        <!-- MQTT fields -->
        <template v-if="dataSourceProtocol === 'MQTT'">
          <el-divider content-position="left">MQTT</el-divider>
          <el-form-item label="Topic">
            <el-input v-model="form.mqttTopic" placeholder="sensors/temperature" />
          </el-form-item>
          <el-form-item label="JSON Path">
            <el-input v-model="form.mqttJsonPath" placeholder="$.value (optional)" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? 'Update' : 'Create' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDataPoints, createDataPoint, updateDataPoint, deleteDataPoint } from '@/api/datapoint'
import { getDataSource } from '@/api/datasource'

const route = useRoute()
const dataSourceId = Number(route.params.dataSourceId)
const dataSourceName = ref('')
const dataSourceProtocol = ref('')
const dataPoints = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()
const editId = ref(null)

const dataTypes = ['BOOLEAN', 'INT16', 'INT32', 'INT64', 'FLOAT', 'DOUBLE', 'STRING']

const emptyForm = () => ({
  tagName: '',
  displayName: '',
  description: '',
  dataType: 'FLOAT',
  opcuaNodeId: '',
  modbusRegisterType: 'HOLDING_REGISTER',
  modbusRegisterAddress: 0,
  mqttTopic: '',
  mqttJsonPath: '',
  sampleIntervalMs: 1000,
  enabled: true
})

const form = ref(emptyForm())

const rules = {
  tagName: [{ required: true, message: 'Tag name is required', trigger: 'blur' }],
  dataType: [{ required: true, message: 'Data type is required', trigger: 'change' }]
}

const fetchDataSource = async () => {
  try {
    const res = await getDataSource(dataSourceId)
    dataSourceName.value = res.data.name
    dataSourceProtocol.value = res.data.protocolType
  } catch (e) {
    console.error(e)
  }
}

const fetchDataPoints = async () => {
  loading.value = true
  try {
    const res = await getDataPoints(dataSourceId)
    dataPoints.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { ...emptyForm(), dataSourceId }
  editId.value = null
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...emptyForm(), ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload = { ...form.value, dataSourceId }
    if (isEdit.value) {
      await updateDataPoint(editId.value, payload)
      ElMessage.success('Updated successfully')
    } else {
      await createDataPoint(payload)
      ElMessage.success('Created successfully')
    }
    dialogVisible.value = false
    await fetchDataPoints()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`Delete data point "${row.tagName}"?`, 'Confirm', { type: 'warning' })
  try {
    await deleteDataPoint(row.id)
    ElMessage.success('Deleted successfully')
    await fetchDataPoints()
  } catch (e) {
    console.error(e)
  }
}

const protocolTagType = (protocol) => {
  const map = { OPCUA: 'primary', MODBUS_TCP: 'warning', MQTT: 'success' }
  return map[protocol] || 'info'
}

onMounted(async () => {
  await fetchDataSource()
  await fetchDataPoints()
})
</script>
