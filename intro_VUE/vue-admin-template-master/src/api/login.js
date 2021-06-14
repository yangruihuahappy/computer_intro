import request from '@/utils/request'

export function login(username, password) {
  return request({
    url: '/introService/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

export function getInfo(token) {
  return request({
    url: '/introService/user/getLoginInfo',
    method: 'get',
    // params: { token }
  })
}

export function logout() {
  return request({
    url: '/introService/user/logout',
    method: 'post'
  })
}
