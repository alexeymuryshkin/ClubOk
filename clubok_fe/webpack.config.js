const packageJSON = require('./package.json');
const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const webpack = require('webpack');

const PATHS = {
    build: path.join(__dirname, '..', 'clubok_be', 'src', 'main', 'resources', 'public', 'assets')
};
module.exports = {
    context: path.join(__dirname, './app'),
    entry: {
        authorization: './jsx/AuthorizationPage.jsx',
        feed: './jsx/FeedPage.jsx'
    },
    module: {
        rules: [
            {
                test: /\.less$/,
                use: ExtractTextPlugin.extract(['css-loader', 'less-loader'])
            },
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: ['babel-loader']
            }
        ]
    },
    resolve: {
        extensions: ['*', '.js', '.jsx']
    },
    output: {
        path: PATHS.build,
        filename: path.normalize('[name].js'),
        publicPath: '/assets/'
    },
    plugins: [
        new ExtractTextPlugin("/[name].css")
    ]
};