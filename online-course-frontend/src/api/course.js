import request from './request'

export function getCourses(params) {
  return request({
    url: '/courses',
    method: 'get',
    params
  })
}

export function getCourse(id) {
  return request({
    url: `/courses/${id}`,
    method: 'get'
  })
}

export function getCourseOutline(id) {
  return request({
    url: `/courses/${id}/outline`,
    method: 'get'
  })
}

export function enrollCourse(id) {
  return request({
    url: `/courses/${id}/enroll`,
    method: 'post'
  })
}

export function getMyCourses(params) {
  return request({
    url: '/courses/my',
    method: 'get',
    params
  })
}

export function getCategories() {
  return request({
    url: '/categories',
    method: 'get'
  })
}