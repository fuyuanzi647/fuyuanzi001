import request from './request'

export function getModuleSummary(apiPrefix) {
  return request.get(`${apiPrefix}/summary`)
}
