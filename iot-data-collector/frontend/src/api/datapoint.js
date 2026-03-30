import request from './index'

export const getDataPoints = (dataSourceId) => request.get('/data-points', { params: { dataSourceId } })
export const getDataPoint = (id) => request.get(`/data-points/${id}`)
export const createDataPoint = (data) => request.post('/data-points', data)
export const updateDataPoint = (id, data) => request.put(`/data-points/${id}`, data)
export const deleteDataPoint = (id) => request.delete(`/data-points/${id}`)
