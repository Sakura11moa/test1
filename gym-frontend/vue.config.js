const { defineConfig } = require('@vue/cli-service')
module.exports = {
  configureWebpack: {
    devtool: 'source-map' // 或者 'inline-source-map'，避免使用 eval
  },
  devServer: {
    proxy: {
      '/api': {// 匹配所有以 '/api1'开头的请求路径
        target: 'http://localhost:9090',// 代理目标的基础路径
        changeOrigin: true,
        // pathRewrite 已移除，保留 /api 前缀让后端直接映射 /api/* 路径
      },
    }
  }
}
