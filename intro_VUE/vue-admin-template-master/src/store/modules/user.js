import loginApi from '@/api/login1'
import { getToken, setToken, removeToken } from '@/utils/auth'
import cookie from 'js-cookie'

const user = {
  state: {
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    admin: false,
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_ADMIN: (state, admin) => {
      state.admin = admin
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      // const username = userInfo.username.trim()
      return new Promise((resolve, reject) => {
        loginApi.submitLogin(userInfo).then(response => {
          const data = response.data
          setToken(data.token)
          commit('SET_TOKEN', data.token)
          commit('SET_ADMIN', data.admin)
          
          cookie.set('intro_token', response.data.token, { domain: 'localhost' })
          cookie.set('intro_admin', response.data.admin, { domain: 'localhost' })
          console.log("看看是不是管理员"+cookie.get('intro_admin'));
          console.log("====token:====="+data.token);
          console.log("====admin:====="+data.admin);
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        console.log("在取用户信息时的token"+state.token);
          loginApi.getLoginInfo().then(response => {
            console.log(response.data.item);
            console.log(response.data.roles);
            console.log(response.data.name);
            if (response.data.roles) { // 验证返回的roles是否是一个非空数组
              commit('SET_ROLES', response.data.roles)
              cookie.set('intro_roles', response.data.roles, { domain: 'localhost' })
              
            } else {
              reject('getInfo: roles must be a non-null array !')
            }
            commit('SET_NAME', response.data.name)
            commit('SET_AVATAR', response.data.avatar)
            resolve(response)
          }).catch(error => {
            reject(error)
          })
      })
    },

    // 登出
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        loginApi.logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 前端 登出
    FedLogOut({ commit }) {
      return new Promise(resolve => {
        commit('SET_TOKEN', '')
        removeToken()
        resolve()
      })
    }
  }
}

export default user
