<template>
  <div class="aui-wrapper aui-page__login">
    <div class="aui-content__wrapper">
      <main class="aui-content">
        <div class="login-header">
          <h2 class="login-brand">{{ $t('brand.lg') }}</h2>
        </div>
        <div class="login-body">
          <h3 class="login-title">{{ $t('register.title') }}</h3>
          <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" status-icon>
            <el-form-item>
              <el-select v-model="$i18n.locale" class="w-percent-100">
                <el-option v-for="(val, key) in i18nMessages" :key="key" :label="val._lang" :value="key"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item prop="username">
              <el-input v-model="dataForm.username" :placeholder="$t('register.username')">
                <span slot="prefix" class="el-input__icon">
                  <svg class="icon-svg" aria-hidden="true"><use xlink:href="#icon-user"></use></svg>
                </span>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="dataForm.password" type="password" :placeholder="$t('register.password')">
                <span slot="prefix" class="el-input__icon">
                  <svg class="icon-svg" aria-hidden="true"><use xlink:href="#icon-lock"></use></svg>
                </span>
              </el-input>
            </el-form-item>
            <el-form-item prop="checkPassword">
              <el-input v-model="dataForm.checkPassword" type="password" :placeholder="$t('register.checkPassword')">
                <span slot="prefix" class="el-input__icon">
                  <svg class="icon-svg" aria-hidden="true"><use xlink:href="#icon-lock"></use></svg>
                </span>
              </el-input>
            </el-form-item>
            <el-form-item prop="captcha">
              <el-row :gutter="20">
                <el-col :span="14">
                  <el-input v-model="dataForm.captcha" :placeholder="$t('login.captcha')">
                    <span slot="prefix" class="el-input__icon">
                      <svg class="icon-svg" aria-hidden="true"><use xlink:href="#icon-safetycertificate"></use></svg>
                    </span>
                  </el-input>
                </el-col>
                <el-col :span="10" class="login-captcha">
                  <img :src="captchaPath" @click="getCaptcha()">
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="dataFormSubmitHandle()" class="w-percent-100">{{ $t('register.title') }}</el-button>
            </el-form-item>
              <p><a @click="robackLogin()" target="_blank">{{ $t('register.roback') }}</a></p>
          </el-form>
        </div>
        <div class="login-footer">
          <p>2020 © 95h·私人订制</p>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
// import Cookies from 'js-cookie'
import debounce from 'lodash/debounce'
import { messages } from '@/i18n'
import { getUUID } from '@/utils'
export default {
  data () {
    return {
      i18nMessages: messages,
      captchaPath: '',
      dataForm: {
        username: '',
        password: '',
        checkPassword: '',
        uuid: '',
        captcha: ''
      }
    }
  },
  computed: {
    dataRule () {
      let password = (rule, value, callback) => {
        if (value === '') {
          callback(new Error(this.$t('validate.required')))
        } else {
          if (this.dataForm.checkPassword !== '') {
            this.$refs.dataForm.validateField('checkPassword')
          }
          callback()
        }
      }
      let checkPassword = (rule, value, callback) => {
        if (value === '') {
          callback(new Error(this.$t('validate.required')))
        } else if (value !== this.dataForm.password) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      }
      return {
        username: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        password: [
          { validator: password, trigger: 'blur' }
        ],
        checkPassword: [
          { validator: checkPassword, trigger: 'blur' }
        ],
        captcha: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    this.getCaptcha()
  },
  methods: {
    // 获取验证码
    getCaptcha () {
      this.dataForm.uuid = getUUID()
      this.captchaPath = `${window.SITE_CONFIG['apiURL']}/captcha?uuid=${this.dataForm.uuid}`
    },
    // 跳转登录页面
    robackLogin () {
      this.$router.replace({ name: 'login' })
    },
    // 表单提交
    dataFormSubmitHandle: debounce(function () {
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return false
        }
        this.$http.post('/register', this.dataForm).then(({ data: res }) => {
          if (res.code !== 0) {
            this.getCaptcha()
            return this.$message.error(res.msg)
          }
          // Cookies.set('token', res.data.token)
          this.$message.success('注册成功!')
          this.$router.replace({ name: 'login' })
        }).catch(() => {
        //   this.$message.error('注册失败!请重新注册')
        //   this.$router.replace({ name: 'register' })
        })
      })
    }, 1000, { 'leading': true, 'trailing': false })
  }
}
</script>
