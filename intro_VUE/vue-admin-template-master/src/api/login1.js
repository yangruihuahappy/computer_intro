import request from '@/utils/request'
import cookie from 'js-cookie'

export default {
  //登录
  submitLogin(userInfo) {
    return request({
      url: `/introService/user/login`,
      method: 'post',
      data: userInfo
    })
  },
  //根据token获取学生信息
  getLoginInfo() {
    return request({
      url: `/introService/user/getLoginInfo`,
      method: 'get',
      headers: {'admin': cookie.get('intro_admin')}
    })
    //headers: {'token': cookie.get('guli_token')} 
  },

 

  logout() {
    return request({
      url: '/introService/user/logout',
      method: 'post'
    })
  }
}