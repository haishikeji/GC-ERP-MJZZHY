<template>
	<view class="bg-white">
		<cu-custom :bgColor="NavBarColor" :isBack="true" backRouterName="index">
			<block slot="content">查询列表</block>
		</cu-custom>
		<view class="container">
			<mescroll-uni ref="mescrollRef" @init="mescrollInit" :top="top" @down="downCallback" @up="upCallback">
				<view class="cu-list menu-avatar">
					<view class="cu-item" :class="modalName=='move-box-'+ index" v-for="(item,index) in dataList"
						:key="index" @touchstart="ListTouchStart" @touchmove="ListTouchMove" @touchend="ListTouchEnd"
						:data-target="'move-box-' + index" style="height:105px" @tap="viewItem(item)">
						<image src="/static/home/lingjian.png" class="cu-avatar lg" mode="scaleToFill"></image>
						<view class="content">
							<view class="text-grey" style="font-size: 45upx;">
								{{item.mouldCode}}
							</view>
							<view class="text-grey" style="font-size: 35upx;">
								{{item.sort}}_{{item.partName}}
							</view>
						</view>
						<view class="action">
							<view class="text-grey text-xs" style="font-size: 45upx;">{{item.storeStatus}}</view>
						</view>
					</view>
				</view>
			</mescroll-uni>
		</view>

		<div class="btn-qr2">
			<table style="width:100%" cellspacing="0">
				<tr>
					<td style="width:50%"> <button type="primary" @click="addItem()">扫码</button></td>
					<td style="width:50%"> <button type="primary" @click="onSetting()">搜索</button></td>
				</tr>
			</table>
		</div>

		<view class="cu-load load-modal" v-if="loading">
			<image src="/static/home/login_bk.png" mode="aspectFit" class="round"></image>
			<view class="gray-text">加载中...</view>
		</view>

		<view>
			<!-- 输入框示例 -->
			<uni-popup ref="inputDialog" type="dialog">
				<uni-popup-dialog ref="inputClose" mode="input" title="模具编码或加工件名称" :value="cardId"
					placeholder="自动适配:模具,自动化,单件,维修." @confirm="confirmPop" @close="cancelPop">
				</uni-popup-dialog>
			</uni-popup>
		</view>

	</view>
</template>

<script>
	import MescrollMixin from "@/components/mescroll-uni/mescroll-mixins.js";
	export default {
		mixins: [MescrollMixin], // 使用mixin
		data() {
			return {
				scrollLeft: 0,
				NavBarColor: this.NavBarColor,
				dataList: [], //列表数据
				url: "/app/query/getList", //列表数据地址

				listTouchStart: 0,
				listTouchStartY: 0,
				modalName: null,
				listTouchDirection: null,

				loading: false,
				// 2022-7-20 工艺卡id
				cardId: '',
				process_incomplete: 1,
				process_complete: 0,
				showPop: false,
				mould_code: ''
			}
		},
		onShow() {
			if (this.mescroll) {
				this.mescroll.resetUpScroll()
			}
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
			formatDate(date) {
				// console.log(date)
				let newDate = new Date(date);
				let year = newDate.getFullYear();
				if (Number.isNaN(year)) {
					return ''
				}
				let month = newDate.getMonth().toString().padStart(2, 0);
				let day = newDate.getDay().toString().padStart(2, 0);
				return year + '-' + month + '-' + day;
			},
			/**
			 * 下拉刷新后回调方法
			 * @param {Object} page
			 */
			upCallback(page) {
				console.log('upCallback')
				// console.log(("l".length))
				// console.log(("A".length))
				// console.log(("1".length))
				// console.log(("林".length))
				//联网加载数据
				let keyword = 0
				if (keyword == 0) {
					let employeeId = this.$store.state.userid
					let isInspectionRole = this.$store.state.isInspectionRole
					if (isInspectionRole == false) {
						employeeId = ""
					}
					console.log("list-employeeId>", employeeId + "||" + isInspectionRole)
					this.loading = true
					this.$http.post(this.url, {
						pn: page.num,
						pageSize: page.size,
						mould_code: this.mould_code
					}).then(res => {
						this.loading = false
						//联网成功的回调,隐藏下拉刷新和上拉加载的状态;
						console.log("res", res)
						let tmpList = res.data.data.list
						if (page.num == 1) {
							this.dataList = []; //如果是第一页需手动制空列表
						}
						this.dataList = this.dataList.concat(tmpList); //追加新数据
						this.mescroll.endSuccess(this.dataList.length);

					}).catch(() => {
						this.loading = false
						//联网失败, 结束加载
						this.mescroll.endErr();
					})
				}

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
			/**
			 * 查看数据
			 * @param {Object} item
			 */
			viewItem(item) {
				this.mescroll.resetUpScroll()
				let urlNav = '/pages/query/detail?item=' + encodeURIComponent(JSON
					.stringify(item)) + '&operate=view'
				// console.log("viewItem-urlNav>", urlNav)
				uni.navigateTo({
					url: urlNav
				})
			},
			search() {
				this.$refs.inputDialog.open()
			},
			//弹窗
			confirmPop(val) { //确认
				this.mould_code = val
				if (val != "") {
					this.upCallback({
						num: 1,
						size: 10
					})
				} else {
					this.mould_code = ''
					this.upCallback({
						num: 1,
						size: 10
					})
				}
			},
			cancelPop() { //取消
				console.log('点击了取消')
				this.showPop = false
			},
			onSetting: function() {
				this.$refs.inputDialog.open()
			},
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
</style>
