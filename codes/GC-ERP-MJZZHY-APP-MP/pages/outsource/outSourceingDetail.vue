<template>
	<view class="bg-white">
		<cu-custom :bgColor="NavBarColor" :isBack="true" backRouterName="index">
			<block slot="content">已外协表单</block>
		</cu-custom>
		<view class="cu-list menu-avatar">
			<view class="cu-item">
				<view class="content2">
					<view class="text-grey" style="font-size: 45upx;">
						<table>
							<tr>
								<td style="font-size: 38upx;width:30%"><span style="color:red">*</span>外协商</td>
								<td>
									<companySelect label="外协" placeholder="外协商信息" :dict="outSourceCompany"
										v-model="companyId" :myRequired="true" :myDisabled="true" :operate="operate">
									</companySelect>
								</td>
							</tr>
						</table>
					</view>
				</view>
			</view>
			<view class="cu-list menu-avatar">
				<view class="cu-item" v-for="(item,index) in dataList" :key="index" @touchstart="ListTouchStart"
					@touchmove="ListTouchMove" @touchend="ListTouchEnd" :data-target="'move-box-' + index">
					<!-- <image src="/static/home/todo/3.png" class="cu-avatar lg" mode="scaleToFill"></image> -->
					<image :src="getImgUrl(item)" class="cu-avatar lg" mode="scaleToFill"></image>
					<view class="content" style="width: calc(100% - 96px - 30px - 60px - 10px - 50px);">
						<view class="text-grey" style="font-size: 30upx;">
							{{item.mouldCode}}
						</view>
						<view class="text-grey" style="font-size: 25upx;">
							{{item.sort+"_"+ item.bomPartName}}
						</view>
						<view class="text-grey" style="font-size: 25upx;">
							<span v-for="(process,index) in item.processes" style="margin-right: 4px;">
								<span v-if="index <3">{{process.processSort+"_"+ process.processName}}</span>
								<span v-else-if="index ==3">...</span>
							</span>
						</view>
					</view>
					<view class="action">
						<!-- <view class="text-grey text-xs" style="font-size: 45upx;" @click="handleProcessAdd(item)">添加工序</view> -->
						<!-- <view class="text-grey text-xs">{{item.number}}</view> -->
						<!-- <span>外协数量</span> -->
						<uni-number-box v-model="item.number" :min="parseInt(1,10)" :max="item.maxNumber">
						</uni-number-box>
					</view>
					<view class="action">
						<button class="cu-btn bg-blue lg" @click="handleConfirmSingle(item)">完成</button>
					</view>
					<!-- <view class="move"> -->
					<!-- <view class="bg-blue" @tap.stop="modifyItem(item)">修改</view> -->
					<!--因为追加的工序,都是自动入库的。只有整单删除的时候，才清理自动入库-->
					<!-- <view v-if="dataList.size > 1" class="bg-red" @tap.stop="deleteItem(item)">删除</view> -->
					<!-- </view> -->
				</view>
			</view>

			<!-- 2023-03-14修改,外协完成不是整体完成了,而是一件件的完成 -->
			<!-- 			<view class="padding flex flex-direction" v-if="dataList && dataList.length >=1">
				<button class="cu-btn bg-blue lg" @click="handleConfirm">外协完成</button>
			</view> -->

			<view class="padding flex flex-direction" v-if="dataList && dataList.length >=1">
				<button v-if="false">提交</button>
			</view>
		</view>
		<loadingCom :loading="loading" />
	</view>
</template>

<script>
	import companySelect from './company-select.vue'
	import processSelect from './process-select.vue'
	import loadingCom from '@/pages/loading/loading.vue'
	export default {
		components: {
			companySelect,
			processSelect,
			loadingCom
		},
		onLoad: function(option) {
			console.log('接收到的参数!')
			if (option) {
				if (option.appOutSourceId) {
					this.appOutSourceId = option.appOutSourceId
				}
			}
		},
		data() {
			return {
				loading: false,
				outSourceCompany: [],
				companyId: '',
				companyName: '',
				processId: '',
				NavBarColor: this.NavBarColor,
				dataList: [], //列表数据
				getIndex: "/app/outSource/getList",
				getHome: "/app/outSource/getHome",
				confirm: "/app/outSource/confirm",
				confirmSingle: "/app/outSource/confirmSingle",
				deleteItemUrl: "/app/outSource/deleteItem",
				listTouchStart: 0,
				listTouchStartY: 0,
				modalName: null,
				listTouchDirection: null,
				operate: 'modify',
				disabled: false,
				number: 0,
				username: this.$store.state.username,

				currentPartItem: null,
				processes: [],
				appOutSourceId: '',
			}
		},
		onShow() {
			this.loadHome()
			this.loadIndex()
			// this.loadProcess()
		},
		created() {
			this.loadHome()
			this.loadIndex()
		},
		activated() {
			console.log('我被激活了')
		},
		computed: {
			top() {
				return this.CustomBar * 2
			},
			style() {
				var StatusBar = this.StatusBar;
				var CustomBar = this.CustomBar;
				var style = `height:${CustomBar}px;padding-top:${StatusBar-50}px;`;
				return style
			},
		},
		methods: {
			reload() {
				this.loadHome()
				this.loadIndex()
				this.dataList = []
			},
			loadHome() {
				var that = this
				console.log('loadIndex')
				var param = {}
				this.$http.post(this.getHome, param).then(res => {
					that.outSourceCompany = res.data.data.company
				}).catch((e) => {
					console.log(e)
				})
			},
			// 页面初始的时候，就加载外协商
			loadIndex() {
				var that = this
				console.log('loadIndex')
				var param = {
					appOutSourceId: this.appOutSourceId
				}
				that.dataList = null
				this.$http.post(this.getIndex, param).then(res => {
					var data = res.data.data.list
					if (data && data.length === 1) {
						that.companyId = data[0].companyId
						// 没有外协的明细返回
						// 因为在2023-03-14的时候,添加了单挑确认
						// 为了达到全部确认后，返回主页的效果
						//  弃用
						if (!data[0].parts || data[0].parts == 0) {
							// uni.navigateBack()
						} else {
							that.dataList = data[0].parts
						}
					} else {
						uni.navigateBack()
					}
				}).catch((e) => {
					console.log(e)
				})
			},
			// ListTouch触摸开始
			ListTouchStart(e) {
				this.listTouchStart = e.touches[0].pageX
				this.listTouchStartY = e.touches[0].pageY
			},

			// ListTouch计算方向
			ListTouchMove(e) {
				let subX = e.touches[0].pageX - this.listTouchStart
				let subY = e.touches[0].pageY - this.listTouchStartY
				if (subY > 50 || subY < -50) {
					if (subY > 50) {
						this.listTouchDirection = 'down'
						console.log('下滑')
					} else if (subY < -50) {
						this.listTouchDirection = 'up'
						console.log('上滑')
					}
				} else {
					if (subX > 100) {
						this.listTouchDirection = 'right'
						console.log('右滑')
					} else if (subX < -100) {
						this.listTouchDirection = 'left'
						console.log('左滑')
					} else {
						this.listTouchDirection = ''
						console.log('无效')
					}
				}
			},
			// ListTouch计算滚动
			ListTouchEnd(e) {
				if (this.listTouchDirection == 'left') {
					this.modalName = e.currentTarget.dataset.target
				} else {
					this.modalName = null
				}
				this.listTouchDirection = null
			},
			handleConfirm() {
				var that = this
				var param = {
					id: this.appOutSourceId,
					parts: this.dataList
				}
				this.loading = true
				this.$http.post(this.confirm, param).then(res => {
					if (res.data.code === 200) {
						uni.showToast({
							title: '操作成功',
							icon: 'none'
						})
						this.dataList = []
						uni.navigateBack()
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						})
					}
				}).catch((e) => {
					console.log(e)
				}).finally(() => {
					this.loading = false
				})
			},
			// 2023-03-14 对单条的外协明细确认 
			handleConfirmSingle(item) {
				var arr = []
				arr.push(item)
				var that = this
				var param = {
					id: this.appOutSourceId,
					parts: arr
				}
				this.loading = true
				this.$http.post(this.confirmSingle, param).then(res => {
					if (res.data.code === 200) {
						uni.showToast({
							title: '操作成功',
							icon: 'none'
						})
						this.loadIndex()
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						})
					}
				}).catch((e) => {
					console.log(e)
				}).finally(() => {
					this.loading = false
				})
			},
			filterItem(item) {
				console.log('过滤前:长度' + this.dataList.length)
				var filters = this.dataList.filter(emp => {
					// app_out_source_detail_id
					return emp.id !== item.id
				})
				this.dataList = filters
				console.log('过滤后:长度' + this.dataList.length)
			},
			deleteItem(item) {
				var that = this
				this.loading = true
				this.$http.post(this.deleteItemUrl, item).then(res => {
					if (res.data.code == 200) {
						uni.showToast({
							title: '操作成功',
							icon: 'none'
						})
						that.filterItem(item)
					}
				}).catch((e) => {
					console.log(e)
				}).finally(() => {
					this.loading = false
				})
			},
			getImgUrl(item) {
				var result = "/static/home/lingjian"
				if (item.reportType == '1') {
					result = result + "_m"
				} else if (item.reportType == '2') {
					result = result + "_z"
				} else if (item.reportType == '3') {
					result = result + "_d"
				} else if (item.reportType == '4') {
					result = result + "_x"
				}
				result = result + ".png"
				return result
			}
		}
	}
</script>

<style scoped>
	.e-btn {
		margin-bottom: 1rem;
	}

	.titlePad {
		margin-top: 0.6rem;
	}

	.cu-list>.move-cur {
		transform: translateX(-260rpx);

	}

	.nav .cu-item.cur {
		position: flex;
		z-index: 9;
		border-bottom: 4upx solid;
	}

	.btn-qr {
		font-size: 45upx;
		background-color: #3573e1;
		width: 110upx;
		align-content: center;
	}

	.btn-qr2 {
		width: 100%;
		position: fixed;
		bottom: 0;
		z-index: 10;
	}


	/**添加工序的对话框*/
	.e-btn {
		margin-bottom: 1rem;
	}

	.titlePad {
		margin-top: 0.6rem;
	}

	.cu-list>.move-cur {
		transform: translateX(-260rpx);

	}

	.nav .cu-item.cur {
		position: flex;
		z-index: 9;
		border-bottom: 4upx solid;
	}

	.btn-qr {
		font-size: 45upx;
		background-color: #3573e1;
		width: 110upx;
		align-content: center;
	}

	.btn-qr2 {
		width: 100%;
		position: fixed;
		bottom: 0;
		z-index: 10;
	}

	@mixin height {
		/* #ifndef APP-NVUE */
		height: 100%;
		/* #endif */
		/* #ifdef APP-NVUE */
		flex: 1;
		/* #endif */
	}

	.popup-height {
		height: 100%;
		width: 350px;
	}

	.popup-content {
		flex: 1;
		align-items: center;
		justify-content: center;
		padding: 5px;
		height: 160px;
		background-color: #fff;
	}

	@mixin flex {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
	}
</style>