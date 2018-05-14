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
        authorization: './jsx/Authorization.jsx',
        feed: './jsx/Feed.jsx'
    },
    module: {
        rules: [
            {
                test: /\.scss$/,
                use: [
                    'style-loader',
                    'css-loader',
                    'sass-loader'
                ]
            },
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: ['babel-loader']
            }
        ]
    },
    resolve: {
        alias: {
            'semantic-ui': path.join(__dirname, "node_modules", "semantic-ui-css", "semantic.js"),
        },
        extensions: ['*', '.js', '.jsx']
    },
    output: {
        path: PATHS.build,
        filename: path.normalize('[name].js'),
        publicPath: '/assets/'
    },
    plugins: [
        new ExtractTextPlugin("/[name].css")
    ],
    devServer: {
        contentBase: "../clubok_be/src/main/resources/public",
        hot: true
    },
};