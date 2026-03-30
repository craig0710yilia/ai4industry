import request from './index'

export const getDataSources = () => request.get('/data-sources')
export const getDataSource = (id) => request.get(`/data-sources/${id}`)
export const createDataSource = (data) => request.post('/data-sources', data)
export const updateDataSource = (id, data) => request.put(`/data-sources/${id}`, data)
export const deleteDataSource = (id) => request.delete(`/data-sources/${id}`)
export const startCollection = (id) => request.post(`/data-sources/${id}/start`)
export const stopCollection = (id) => request.post(`/data-sources/${id}/stop`)
export const getCollectionStatus = () => request.get('/data-sources/status')
export const testConnection = (id) => request.post(`/data-sources/${id}/test`)
