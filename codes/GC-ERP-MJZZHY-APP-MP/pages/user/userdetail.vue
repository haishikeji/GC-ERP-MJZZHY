<template>
	<view>
		<scroll-view scroll-y class="page">
			<cu-custom :bgColor="NavBarColor" :isBack="true">
				<block slot="backText">返回</block>
				<block slot="content">用户详情</block>
				<!-- <view slot="right" @tap="rightClick">编辑</view> -->
			</cu-custom>
			<!-- list列表 -->
			<view class="cu-list menu">
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.1s'}]">
					<view class="content">
						<text class="text-grey">头像</text>
					</view>
					<view class="action">
						<view class="cu-avatar round sm" :style="{backgroundImage: 'url(' + personalMsg.avatar + ')'}">
						</view>
					</view>

				</view>
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.2s'}]">
					<view class="content">
						<text class="text-grey">员工姓名</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.name}}</text>
					</view>
				</view>
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.2s'}]">
					<view class="content">
						<text class="text-grey">员工号</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.code}}</text>
					</view>
				</view>
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.3s'}]">
					<view class="content">
						<text class="text-grey">性别</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.gender}}</text>
					</view>
				</view>
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.4s'}]">
					<view class="content">
						<text class="text-grey">生日</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.birthday}}</text>
					</view>
				</view>
			</view>

			<view class="cu-list menu">
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.5s'}]">
					<view class="content">
						<text class="text-grey">职务</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.positionName}}</text>
					</view>
				</view>
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.5s'}]">
					<view class="content">
						<text class="text-grey">学历</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.education}}</text>
					</view>
				</view>
			</view>

			<!-- 			<view class="cu-list menu">
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.5s'}]">
					<view class="content">
						<text class="text-grey">是否是巡查人员</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.isPatrolPerson?"是":"否"}}</text>
					</view>
				</view>
			</view> -->

			<view class="cu-list menu">
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '0.9s'}]">
					<view class="content">
						<text class="text-grey">电话号</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.phone}}</text>
					</view>
				</view>
				<view class="cu-item animation-slide-bottom" :style="[{animationDelay: '1s'}]">
					<view class="content">
						<text class="text-grey">电子邮箱</text>
					</view>
					<view class="action">
						<text class="text-grey">{{personalMsg.email}}</text>
					</view>
				</view>
			</view>



		</scroll-view>
	</view>
</template>

<script>
	import api from '@/api/api.js'
	import store from '@/store/index.js';

	export default {
		data() {
			return {
				personalMsg: {
					cardNo: "",
					photo: "",
					age: "",
					birthday: "",
					phone: "",
					wechatN: "",
					email: "",
					entryTime: "",
					resignationTime: "",
					isPatrolPerson: "",
					isPartTimer: "",
					remark: "",
					positionName: "",
					education: ""
				},
				userUrl: '/app/employee/getById/',
				positionUrl: '/general/position/list',
				departUrl: '/general/user/userDepartList'
			};
		},
		onLoad() {
			this.loadinfo()
		},
		methods: {
			getSubStringText(text, len) {
				if (!text || text.length == 0) {
					return ''
				}
				if (text.length < len) {
					return text;
				}
				return text.substr(0, len) + "..."
			},
			loadinfo() {
				let url = this.userUrl + store.getters.userid;
				console.log("loadinfo-employee>", url)
				this.$http.get(url).then(res => {
					console.log("用户", res)
					let data = res.data.data
					if (data) {
						this.personalMsg = data.formData
					}
				}).catch(e => {
					console.log("请求错误", e)
				})

			},
			rightClick() {

				console.log('123123')
				const _url = '/pages/login/reppwd'
				// uni.redirectTo({
				// 	url: _url,
				// 	success: function(result) {}
				// })
				// this.$Router.push({
				// 	name: '/pages/user/useredit'
				// })
				uni.navigateTo({
					url: _url
				})
			}
		},
	}
</script>

<style>
	.page {
		height: 100Vh;
		width: 100vw;
	}

	.page.show {
		overflow: hidden;
	}

	.switch-sex::after {
		content: "\e716";
	}

	.switch-sex::before {
		content: "\e7a9";
	}

	.switch-music::after {
		content: "\e66a";
	}

	.switch-music::before {
		content: "\e6db";
	}
</style>
