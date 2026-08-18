import request from './request'

export function getUserPage(params) {
  return request.get('/config/user/page', { params })
}

export function getUserDetail(id) {
  return request.get(`/config/user/${id}`)
}

export function createUser(data) {
  return request.post('/config/user', data)
}

export function updateUser(data) {
  return request.put('/config/user', data)
}

export function deleteUser(id) {
  return request.delete(`/config/user/${id}`)
}

export function transferUser(id, positionId, departmentId) {
  return request.put(`/config/user/${id}/transfer`, null, { params: { positionId, departmentId } })
}

export function exportUser() {
  return request.get('/config/user/export')
}

export function importUser(content) {
  return request.post('/config/user/import', { content })
}

export function getPositionPage(params) {
  return request.get('/config/position/page', { params })
}

export function getPositionList() {
  return request.get('/config/position/list')
}

export function createPosition(data) {
  return request.post('/config/position', data)
}

export function updatePosition(data) {
  return request.put('/config/position', data)
}

export function deletePosition(id) {
  return request.delete(`/config/position/${id}`)
}

export function exportPosition() {
  return request.get('/config/position/export')
}

export function importPosition(content) {
  return request.post('/config/position/import', { content })
}

export function getSalesmanPage(params) {
  return request.get('/config/salesman/page', { params })
}

export function createSalesman(data) {
  return request.post('/config/salesman', data)
}

export function updateSalesman(data) {
  return request.put('/config/salesman', data)
}

export function deleteSalesman(id) {
  return request.delete(`/config/salesman/${id}`)
}

export function exportSalesman() {
  return request.get('/config/salesman/export')
}

export function importSalesman(content) {
  return request.post('/config/salesman/import', { content })
}
