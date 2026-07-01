
const CompressionPlugin = require('compression-webpack-plugin');

module.exports = {
    // 关键：设置静态资源相对路径，避免被错误代理到Nexus
    publicPath: './',
    // 开发服务器配置（适配方案A跨域，优化代理规则）
    devServer: {
        host: '0.0.0.0',
        port: 8080,
        proxy: {
            '/zwz': {
                target: 'http://127.0.0.1:8082',
                ws: true,
                changeOrigin: true, // 新增：开启跨域代理（核心）
                pathRewrite: { '^/zwz': '/zwz' } // 新增：确保路径正确转发
            }
        }
    },
    productionSourceMap: false,
    configureWebpack: {
        plugins: [
            new CompressionPlugin({
                test: /\.js$|\.html$|\.css/,
                threshold: 10240
            })
        ]
    }
}
// module.exports = {




    
//     devServer: {
//         host: '0.0.0.0',
//         port: 8080,
//         proxy: {
//             '/zwz': {
//                 target: 'http://127.0.0.1:8082',
//                 ws: true
//             }
//         }
//     },
//     productionSourceMap: false,
//     configureWebpack: {
//         plugins: [
//             new CompressionPlugin({
//                 test: /\.js$|\.html$|\.css/,
//                 threshold: 10240
//             })
//         ]
//     }
// }