<template>
	<view>
		<scroll-view scroll-y class="page">
			<!-- 头部logo-->
			<view class="UCenter-bg">
				<image :src="personal.avatar" class="png round animation-slide-right margin-bottom-sm"
					mode="scaleToFill" :style="[{animationDelay: '0.1s'}]"></image>
				<image src="/static/home/person_bk.gif" mode="scaleToFill" class="gif-wave"></image>
			</view>
			<view class="padding flex text-center text-grey bg-white shadow-warp">
				<view class="flex flex-sub flex-direction solid-right animation-slide-top"
					:style="[{animationDelay: '0.2s'}]">
					<view class="text-xl text-orange">{{personal.username}}</view>
					<view class="margin-top-sm"><text class="cuIcon-people"></text> 用户</view>
				</view>

				<view class="flex flex-sub flex-direction animation-slide-top" :style="[{animationDelay: '0.2s'}]">
					<view class="text-xl text-green">{{personal.positionName?personal.positionName:'员工'}}</view>
					<view class="margin-top-sm"><text class="cuIcon-news"></text> 职务</view>
				</view>
			</view>
			<!-- 列表list-->
			<view class="cu-list menu card-menu margin-top-xl margin-bottom-xl shadow-lg radius">
				<!-- <view class="cu-item arrow animation-slide-bottom" :style="[{animationDelay: '0.1s'}]">
		      <view class="content" >
		        <text class="cuIcon-favorfill text-yellow"></text>
		        <text class="text-grey">收藏</text>
		      </view>
		    </view>
		    <view class="cu-item arrow animation-slide-bottom" :style="[{animationDelay: '0.2s'}]">
		      <view class="content">
		        <text class="cuIcon-redpacket_fill text-red"></text>
		        <text class="text-grey">红包</text>
		      </view>
		    </view> -->
				<!-- <view class="cu-item arrow animation-slide-bottom" :style="[{animationDelay: '0.3s'}]" @tap="scan">
			  <view class="content">
			    <text class="cuIcon-scan text-red"></text>
			    <text class="text-grey">扫码</text>
			  </view>
			</view> -->
				<!-- 			<navigator class="cu-item arrow animation-slide-bottom" :style="[{animationDelay: '0.4s'}]" url="/pages/user/location" hover-class="none">
				<view class="content" >
				    <text class="cuIcon-location text-cyan"></text>
					<text class="text-grey">定位</text>
				</view>
			</navigator> -->
				<!-- 			<navigator class="cu-item arrow animation-slide-bottom" url="/pages/user/userdetail" :style="[{animationDelay: '0.6s'}]">
			     <view class="content">
				    <text class="cuIcon-settingsfill text-cyan"></text>
					<text class="text-grey">设置</text>
			    </view>
			</navigator> -->

				<navigator class="cu-item arrow animation-slide-bottom" :style="[{animationDelay: '0.7s'}]"
					url="/pages/user/userexit" hover-class="none">
					<view class="content">
						<text class="cuIcon-exit text-cyan"></text>
						<text class="text-grey">退出</text>
					</view>
				</navigator>
			</view>
			<view class="cu-tabbar-height"></view>
		</scroll-view>
	</view>
</template>

<script>
	import api from '@/api/api'
	import store from '@/store/index.js';
	//import {ACCESS_TOKEN} from '@/common/util/constants.js'

	// const getTokenStorage = () => {
	// 	let token = ''
	// 	try{
	// 		token = uni.getStorageSync(ACCESS_TOKEN)
	// 	}catch(e){
	// 		// handle the exception
	// 		console.log("getTokenStorage",token)
	// 	}
	// 	return token
	// }

	export default {
		name: "people",
		data() {
			return {
				personal: {
					avatar: (store.state ? store.state.avatar : ""),
					realname: (store.state ? store.state.realname : ""),
					username: (store.state ? store.state.username : ""),
					positionName: "",
				},
				positionUrl: '/general/position/list',
				departUrl: '/general/user/userDepartList',
				userUrl: '/general/authorization/getInfo',
				postUrl: '/general/position/queryByCode',
				userId: '',
				id: ''
			}
		},
		watch: {
			cur: {
				immediate: true,
				handler() {
					// console.log('watch',this.cur)
					//    this.userId=this.$store.getters.userid;
					// this.load()
				},
			},
		},

		methods: {}
	}
</script>

<style>
	.UCenter-bg {
		/* #ifdef MP-WEIXIN */
		/* 	background-image: url('https://static.jeecg.com/upload/test/blue_1595818030310.png');
 */
		/* #endif */
		/* #ifndef MP-WEIXIN */
		/* #endif */
		background: #3573e1;
		background-size: cover;
		height: 400rpx;
		display: flex;
		justify-content: center;
		padding-top: 40rpx;
		overflow: hidden;
		position: relative;
		flex-direction: column;
		align-items: center;
		color: #fff;
		font-weight: 300;
		text-shadow: 0 0 3px rgba(0, 0, 0, 0.3);
	}

	.UCenter-bg text {
		opacity: 0.8;
	}

	.UCenter-bg image {
		width: 200rpx;
		height: 200rpx;
	}

	.UCenter-bg .gif-wave {
		position: absolute;
		width: 100%;
		bottom: 0;
		left: 0;
		z-index: 99;
		mix-blend-mode: screen;
		height: 100rpx;
	}

	map,
	.mapBox {
		left: 0;
		z-index: 99;
		mix-blend-mode: screen;
		height: 100rpx;
	}

	map,
	.mapBox {
		width: 750rpx;
		height: 300rpx;
	}
</style>