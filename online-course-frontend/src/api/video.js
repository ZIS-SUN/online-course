import request from './request'

export function getVideo(id) {
  return request({
    url: `/videos/${id}`,
    method: 'get'
  })
}

export function getVideoProgress(id) {
  return request({
    url: `/videos/${id}/progress`,
    method: 'get'
  })
}

export function updateVideoProgress(id, data) {
  return request({
    url: `/videos/${id}/progress`,
    method: 'patch',
    data
  })
}