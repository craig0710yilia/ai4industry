<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 16px; font-weight: bold;">Real-time Monitor</span>
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-select v-model="selectedDataSourceId" placeholder="Select Data Source" style="width: 220px;"
                       @change="onDataSourceChange">
              <el-option v-for="ds in dataSources" :key="ds.id" :label="ds.name" :value="ds.id" />
            </el-select>
            <el-tag :type="autoRefreshActive ? 'success' : 'info'">
              {{ autoRefreshActive ? 'Auto-refresh ON' : 'Auto-refresh OFF' }}
            </el-tag>
            <el-button @click="toggleAutoRefresh" :type="autoRefreshActive ? 'warning' : 'primary'">
              {{ autoRefreshActive ? 'Stop Refresh' : 'Start Refresh' }}
            </el-button>
            <el-button @click="fetchLatestData" :loading="loading">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
        </div>
      </template>

      <el-empty v-if="!selectedDataSourceId" description="Please select a data source" />

      <el-table v-else :data="latestData" v-loading="loading" stripe border>
        <el-table-column prop="tagName" label="Tag Name" min-width="150" />
        <el-table-column prop="displayName" label="Display Name" min-width="150" />
        <el-table-column label="Value" min-width="150">
          <template #default="{ row }">
            <span style="font-weight: bold; font-size: 14px;">
              {{ row.rawValue !== null && row.rawValue !== undefined ? row.rawValue : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="Quality" width="110">
          <template #default="{ row }">
            <el-tag :type="qualityTagType(row.quality)" size="small">
              {{ qualityLabel(row.quality) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Collected At" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.collectedAt) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDataSources } from '@/api/datasource'
import { getLatestAllPoints } from '@/api/collecteddata'

const dataSources = ref([])
const selectedDataSourceId = ref(null)
const latestData = ref([])
const loading = ref(false)
const autoRefreshActive = ref(false)
let refreshTimer = null

const fetchDataSources = async () => {
  try {
    const res = await getDataSources()
    dataSources.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const fetchLatestData = async () => {
  if (!selectedDataSourceId.value) return
  loading.value = true
  try {
    const res = await getLatestAllPoints(selectedDataSourceId.value)
    latestData.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const onDataSourceChange = () => {
  latestData.value = []
  fetchLatestData()
}

const toggleAutoRefresh = () => {
  if (autoRefreshActive.value) {
    clearInterval(refreshTimer)
    refreshTimer = null
    autoRefreshActive.value = false
  } else {
    if (!selectedDataSourceId.value) {
      ElMessage.warning('Please select a data source first')
      return
    }
    autoRefreshActive.value = true
    refreshTimer = setInterval(fetchLatestData, 5000)
    fetchLatestData()
  }
}

const qualityLabel = (quality) => {
  const map = { 0: 'Good', 1: 'Uncertain', 2: 'Bad' }
  return map[quality] ?? 'Unknown'
}

const qualityTagType = (quality) => {
  const map = { 0: 'success', 1: 'warning', 2: 'danger' }
  return map[quality] ?? 'info'
}

const formatDateTime = (dt) => {
  if (!dt) return '-'
  return new Date(dt).toLocaleString()
}

onMounted(async () => {
  await fetchDataSources()
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})
</script>
