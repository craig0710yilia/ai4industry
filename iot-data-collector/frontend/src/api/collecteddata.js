import request from './index'

export const getLatestAllPoints = (dataSourceId) =>
  request.get('/collected-data/latest', { params: { dataSourceId } })

export const getLatestByPoint = (pointId, limit = 50) =>
  request.get(`/collected-data/point/${pointId}/latest`, { params: { limit } })

export const queryByTimeRange = (pointId, from, to, page = 1, size = 100) =>
  request.get(`/collected-data/point/${pointId}`, { params: { from, to, page, size } })
