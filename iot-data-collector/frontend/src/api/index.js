import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 10000
})

request.interceptors.response.use(
  response => {
    const data = response.data
    if (data.code && data.code !== 200) {
      ElMessage.error(data.message || 'Request failed')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  error => {
    const message = error.response?.data?.message || error.message || 'Network error'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
