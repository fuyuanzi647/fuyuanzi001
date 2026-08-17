import request from './request'

export function getShipmentPage(params) {
  return request.get('/payment/shipment/page', { params })
}

export function getShipmentDetail(id) {
  return request.get(`/payment/shipment/${id}`)
}

export function createShipment(data) {
  return request.post('/payment/shipment', data)
}

export function updateShipment(data) {
  return request.put('/payment/shipment', data)
}

export function deleteShipment(id) {
  return request.delete(`/payment/shipment/${id}`)
}

export function addShipmentPayment(data) {
  return request.post('/payment/shipment/payment', data)
}

export function getOrderPayments(orderId) {
  return request.get(`/payment/shipment/${orderId}/payment`)
}

export function getPaymentOptions() {
  return request.get('/payment/options')
}
