<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 16px; font-weight: bold;">Database Backups</span>
          <div>
            <el-button @click="fetchBackups" style="margin-right: 8px;">
              <el-icon><Refresh /></el-icon> Refresh
            </el-button>
            <el-button type="primary" @click="triggerBackup" :loading="triggering">
              <el-icon><Download /></el-icon> Trigger Manual Backup
            </el-button>
          </div>
        </div>
      </template>

      <el-alert
        title="Backups are stored as PostgreSQL dump files (.sql). The scheduled backup runs on the 1st of each month at 2:00 AM."
        type="info"
        :closable="false"
        style="margin-bottom: 16px;"
      />

      <el-table :data="backups" v-loading="loading" stripe border>
        <el-table-column label="#" type="index" width="60" />
        <el-table-column label="Backup File" min-width="300">
          <template #default="{ row }">
            <el-icon style="margin-right: 6px; color: #409EFF;"><Document /></el-icon>
            {{ row }}
          </template>
        </el-table-column>
        <el-table-column label="Created" min-width="200">
          <template #default="{ row }">
            {{ parseBackupDate(row) }}
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && backups.length === 0" description="No backups found" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/index'

const backups = ref([])
const loading = ref(false)
const triggering = ref(false)

const fetchBackups = async () => {
  loading.value = true
  try {
    const res = await request.get('/backups')
    backups.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const triggerBackup = async () => {
  triggering.value = true
  try {
    await request.post('/backups/trigger')
    ElMessage.success('Backup triggered successfully')
    await fetchBackups()
  } catch (e) {
    console.error(e)
  } finally {
    triggering.value = false
  }
}

const parseBackupDate = (filename) => {
  // Extract timestamp from filename like backup_20260101_020000.sql
  const match = filename.match(/backup_(\d{4})(\d{2})(\d{2})_(\d{2})(\d{2})(\d{2})/)
  if (match) {
    const [, year, month, day, hour, minute, second] = match
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`
  }
  return '-'
}

onMounted(async () => {
  await fetchBackups()
})
</script>
