<template>
	<view>
		<scroll-view :scroll-y="modalName==null" class="page" :class="modalName!=null?'show':''">
			<cu-custom :bgColor="NavBarColor" :isBack="true">
				<block slot="content">冲压入库详情</block>
				<!-- <view v-if="operate=='view'&&formData!=null&&formData.status=='未开始'" slot="right" @tap="modifyItem()">编辑</view> -->
			</cu-custom>
			<uni-forms ref="valiForm" :modelValue="formData" labelWidth="100px">
				<view class="example">
					<uni-forms-item label="零件编码">
						<view class="cu-form-group" style="z-index:10">
							<view class="flex align-center">
								{{formData.partsCode}}
							</view>
						</view>
					</uni-forms-item>
					<uni-forms-item label="零件名称">
						<view class="cu-form-group" style="z-index:10">
							<view class="flex align-center">
								{{formData.partsName}}
							</view>
						</view>
					</uni-forms-item>
					
					<uni-forms-item label="材质名称">
						<view class="cu-form-group" style="z-index:10">
							<view class="flex align-center">
								{{formData.baseMaterialName}}
							</view>
						</view>
					</uni-forms-item>
					
					<uni-forms-item label="质检次数">
						<view class="cu-form-group" style="z-index:10">
							<view class="flex align-center">
								{{formData.qualityCount}}
							</view>
						</view>
					</uni-forms-item>
					
					<uni-forms-item label="合计数量">
						<view class="cu-form-group" style="z-index:10">
							<view class="flex align-center">
								{{formData.qualityNumber}}
							</view>
						</view>
					</uni-forms-item>
					
					<uni-forms-item label="合计质检合格数量">
						<view class="cu-form-group" style="z-index:10">
							<view class="flex align-center">
								{{formData.qualityQualified}}
							</view>
						</view>
					</uni-forms-item>
					
					<uni-forms-item label="合计让步接受数量">
						<view class="cu-form-group" style="z-index:10">
							<view class="flex align-center">
								{{formData.qualityConcession}}
							</view>
						</view>
					</uni-forms-item>
					
					<uni-forms-item label="合计废品数量">
						<view class="cu-form-group" style="z-index:10">
							<view class="flex align-center">
								{{formData.qualityUnqualified}}
							</view>
						</view>
					</uni-forms-item>
					
				</view>
			</uni-forms>
			<button v-if="operate=='modify'" type="primary" @click="submit('valiForm')">保存</button>
			<button v-if="operate=='add'" type="primary" @click="submit('valiForm')">保存</button>
			<button v-if="operate=='view'&&formData.status=='未入库'" type="primary" @click="submit('valiForm')">确定入库</button>
		</scroll-view>
	</view>

</template>

<script>
	export default {
		onLoad: function(option) {
			console.log("options>", option)
			//获取参数信息
			let item = null;
			if (option) {
				if (option.item) {
					//获取修改数据内容
					item = JSON.parse(decodeURIComponent(option.item));
					if (item) {
						this.formData = item
					};
				}

				if (option.operate) {
					//获取操作类型（add:新增，modify:修改，view:查看）
					this.operate = decodeURIComponent(option.operate);
					if (this.operate == "view") {
						this.disabled = true;
					} else if (this.operate == "add") {
						console.log("load-formData>", this.formData)
					}
					console.log("options-operate>", this.operate)
				}
			}
		},
		data() {
			return {
				NavBarColor: this.NavBarColor,
				modalName: null,
				operate: 'add', //获取操作类型（add:新增，modify:修改，view:查看）
				saveUrl: "/app/appStamp/InStore/save", //保存更新地址
				formData: {}
			}
		},
		methods: {
			modifyItem() {
				this.operate = "modify"
				this.disabled = false
			},
			submit() {
				let employeeId = this.$store.state.userid
				this.formData.employeeId = employeeId;
				if (this.operate == 'modify' || this.operate == 'modify') //保存
				{
				
				} else if (this.operate == 'view' && this.formData.status == '未入库') //开始
				{
					this.formData.status = '已入库'
				}
				
				this.$http.post(this.saveUrl, this.formData).then(res => {
					uni.showToast({
						title: '操作成功',
						icon: 'none'
					})
					uni.navigateBack()
				}).catch((e) => {
					console.log('保存失败:', e)
					this.$tip.error('保存失败:' + e)
				})

			},
		},
	}
</script>

<style>
	.example {
		padding: 5px;
		background-color: #fff;
	}

	.button {
		display: flex;
		align-items: center;
		height: 35px;
		margin-left: 10px;
	}
</style>
