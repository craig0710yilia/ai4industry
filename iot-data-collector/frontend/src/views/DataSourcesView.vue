<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 16px; font-weight: bold;">Data Sources</span>
          <div>
            <el-button @click="fetchStatus" :loading="statusLoading" style="margin-right: 8px;">
              <el-icon><Refresh /></el-icon> Refresh Status
            </el-button>
            <el-button type="primary" @click="openAddDialog">
              <el-icon><Plus /></el-icon> Add Data Source
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="dataSources" v-loading="loading" stripe border>
        <el-table-column prop="name" label="Name" min-width="140" />
        <el-table-column prop="protocolType" label="Protocol" width="120">
          <template #default="{ row }">
            <el-tag :type="protocolTagType(row.protocolType)">{{ row.protocolType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Host:Port" width="180">
          <template #default="{ row }">
            <span v-if="row.host">{{ row.host }}:{{ row.port }}</span>
            <span v-else-if="row.mqttBrokerUrl">{{ row.mqttBrokerUrl }}</span>
            <span v-else-if="row.opcuaEndpointUrl">{{ row.opcuaEndpointUrl }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="Status" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.status || 'STOPPED' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="Enabled" width="90">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? 'Yes' : 'No' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="320" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDataPoints(row)">
              <el-icon><List /></el-icon> Points
            </el-button>
            <el-button size="small" type="primary" @click="openEditDialog(row)">Edit</el-button>
            <el-button
              size="small"
              :type="row.status === 'RUNNING' ? 'warning' : 'success'"
              @click="toggleCollection(row)"
            >
              {{ row.status === 'RUNNING' ? 'Stop' : 'Start' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Data Source' : 'Add Data Source'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="160px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" placeholder="Unique data source name" />
        </el-form-item>
        <el-form-item label="Protocol Type" prop="protocolType">
          <el-select v-model="form.protocolType" placeholder="Select protocol" style="width: 100%;">
            <el-option label="OPC-UA" value="OPCUA" />
            <el-option label="Modbus TCP" value="MODBUS_TCP" />
            <el-option label="MQTT" value="MQTT" />
          </el-select>
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="Enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>

        <!-- OPC-UA fields -->
        <template v-if="form.protocolType === 'OPCUA'">
          <el-divider content-position="left">OPC-UA Settings</el-divider>
          <el-form-item label="Endpoint URL">
            <el-input v-model="form.opcuaEndpointUrl" placeholder="opc.tcp://host:4840" />
          </el-form-item>
          <el-form-item label="Security Mode">
            <el-select v-model="form.opcuaSecurityMode" style="width: 100%;">
              <el-option label="None" value="None" />
              <el-option label="Sign" value="Sign" />
              <el-option label="SignAndEncrypt" value="SignAndEncrypt" />
            </el-select>
          </el-form-item>
        </template>

        <!-- Modbus TCP fields -->
        <template v-if="form.protocolType === 'MODBUS_TCP'">
          <el-divider content-position="left">Modbus TCP Settings</el-divider>
          <el-form-item label="Host">
            <el-input v-model="form.host" placeholder="192.168.1.100" />
          </el-form-item>
          <el-form-item label="Port">
            <el-input-number v-model="form.port" :min="1" :max="65535" :default="502" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="Slave ID">
            <el-input-number v-model="form.modbusSlaveId" :min="1" :max="247" style="width: 100%;" />
          </el-form-item>
        </template>

        <!-- MQTT fields -->
        <template v-if="form.protocolType === 'MQTT'">
          <el-divider content-position="left">MQTT Settings</el-divider>
          <el-form-item label="Broker URL">
            <el-input v-model="form.mqttBrokerUrl" placeholder="tcp://broker:1883" />
          </el-form-item>
          <el-form-item label="Client ID">
            <el-input v-model="form.mqttClientId" placeholder="Optional client ID" />
          </el-form-item>
          <el-form-item label="Username">
            <el-input v-model="form.mqttUsername" />
          </el-form-item>
          <el-form-item label="Password">
            <el-input v-model="form.mqttPassword" type="password" show-password />
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDataSources, createDataSource, updateDataSource,
  deleteDataSource, startCollection, stopCollection, getCollectionStatus
} from '@/api/datasource'

const router = useRouter()
const dataSources = ref([])
const loading = ref(false)
const statusLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const emptyForm = () => ({
  name: '',
  protocolType: '',
  host: '',
  port: 502,
  opcuaEndpointUrl: '',
  opcuaSecurityMode: 'None',
  modbusSlaveId: 1,
  mqttBrokerUrl: '',
  mqttClientId: '',
  mqttUsername: '',
  mqttPassword: '',
  description: '',
  enabled: true
})

const form = ref(emptyForm())
const editId = ref(null)

const rules = {
  name: [{ required: true, message: 'Name is required', trigger: 'blur' }],
  protocolType: [{ required: true, message: 'Protocol type is required', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getDataSources()
    dataSources.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchStatus = async () => {
  statusLoading.value = true
  try {
    const res = await getCollectionStatus()
    const statusMap = res.data || {}
    dataSources.value = dataSources.value.map(ds => ({
      ...ds,
      status: statusMap[ds.id] || 'STOPPED'
    }))
  } catch (e) {
    console.error(e)
  } finally {
    statusLoading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = emptyForm()
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
    if (isEdit.value) {
      await updateDataSource(editId.value, form.value)
      ElMessage.success('Updated successfully')
    } else {
      await createDataSource(form.value)
      ElMessage.success('Created successfully')
    }
    dialogVisible.value = false
    await fetchData()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`Delete data source "${row.name}"?`, 'Confirm', { type: 'warning' })
  try {
    await deleteDataSource(row.id)
    ElMessage.success('Deleted successfully')
    await fetchData()
  } catch (e) {
    console.error(e)
  }
}

const toggleCollection = async (row) => {
  try {
    if (row.status === 'RUNNING') {
      await stopCollection(row.id)
      ElMessage.success('Collection stopped')
    } else {
      await startCollection(row.id)
      ElMessage.success('Collection started')
    }
    await fetchData()
    await fetchStatus()
  } catch (e) {
    console.error(e)
  }
}

const viewDataPoints = (row) => {
  router.push(`/data-points/${row.id}`)
}

const protocolTagType = (protocol) => {
  const map = { OPCUA: 'primary', MODBUS_TCP: 'warning', MQTT: 'success' }
  return map[protocol] || 'info'
}

const statusTagType = (status) => {
  const map = { RUNNING: 'success', STOPPED: 'info', ERROR: 'danger' }
  return map[status] || 'info'
}

onMounted(async () => {
  await fetchData()
  await fetchStatus()
})
</script>
