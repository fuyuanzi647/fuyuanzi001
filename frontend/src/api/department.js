import request from './request'

export function getDepartmentPage(params) {
  return request.get('/config/department/page', { params })
}

export function getDepartmentList() {
  return request.get('/config/department/list')
}

export function getDepartmentDetail(id) {
  return request.get(`/config/department/${id}`)
}

export function createDepartment(data) {
  return request.post('/config/department', data)
}

export function updateDepartment(data) {
  return request.put('/config/department', data)
}

export function deleteDepartment(id) {
  return request.delete(`/config/department/${id}`)
}

export function exportDepartment() {
  return request.get('/config/department/export')
}

export function importDepartment(content) {
  return request.post('/config/department/import', { content })
}
