<template>
	<view>
		<cu-custom :bgColor="NavBarColor" :isBack="true">
			<block slot="backText">返回</block>
			<block slot="content">定位</block>
		</cu-custom>
		<map style="width: 100%; height:100vh;" :latitude="latitude" :longitude="longitude" :markers="marker"
			:scale="scale" :circles="circles">
		</map>
	</view>
</template>

<script>
	// 定位开启状态 true=开启，false=未开启
	let bool = false
	
	// android平台
	if (uni.getSystemInfoSync().platform == 'android') {
		var context = plus.android.importClass("android.content.Context");
		var locationManager = plus.android.importClass("android.location.LocationManager");
		var main = plus.android.runtimeMainActivity();
		var mainSvr = main.getSystemService(context.LOCATION_SERVICE);
		bool = mainSvr.isProviderEnabled(locationManager.GPS_PROVIDER)
	}
	
	// ios平台
	if (uni.getSystemInfoSync().platform == 'ios') {
		var cllocationManger = plus.ios.import("CLLocationManager");
		var enable = cllocationManger.locationServicesEnabled();
		var status = cllocationManger.authorizationStatus();
		plus.ios.deleteObject(cllocationManger);
		bool = enable && status != 2
	}
	
	// 未开启定位功能
	if (bool === false) {
		uni.showModal({
			title: '提示',
			content: '请打开定位服务',
			success: ({
				confirm,
				cancel
			}) => {
	
				if (confirm) {
					// android平台
					if (uni.getSystemInfoSync().platform == 'android') {
						var Intent = plus.android.importClass('android.content.Intent');
						var Settings = plus.android.importClass('android.provider.Settings');
						var intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						var main = plus.android.runtimeMainActivity();
						main.startActivity(intent); // 打开系统设置GPS服务页面
					}
	
					// ios平台
					if (uni.getSystemInfoSync().platform == 'ios') {
						var UIApplication = plus.ios.import("UIApplication");
						var application2 = UIApplication.sharedApplication();
						var NSURL2 = plus.ios.import("NSURL");
						var setting2 = NSURL2.URLWithString("App-Prefs:root=Privacy&path=LOCATION");
						application2.openURL(setting2);
						plus.ios.deleteObject(setting2);
						plus.ios.deleteObject(NSURL2);
						plus.ios.deleteObject(application2);
					}
				}
	
				// 用户取消前往开启定位服务
				if (cancel) {
					// do sth...
				}
	
	
				if (getCurrentPages() > 1) {
		 		uni.navigateBack()
				} else {
					this.$Router.replaceAll({
						name: 'index'
					})
				}
			}
		});
	}
	export default {
		data() {
			return {
				NavBarColor: this.NavBarColor,
				id: 0, // 使用 marker点击事件 需要填写id
				title: 'map',
				latitude: "", //纬度
				longitude: "", //经度
				marker: [],
				scale: 16, //地图缩放程度
				circles: [],
			}
		},
		onLoad() {
			this.getLocation()
		},
		methods: {
			getLocation() {
				let _this = this;
				uni.getLocation({
					type: 'gcj02',
					success: function(res) {
						console.log('当前位置：' + JSON.stringify(res));
						if (res) {
							_this.longitude = res.longitude;
							_this.latitude = res.latitude;
							_this.marker = [{
								id: 0,
								latitude: res.latitude, //纬度
								longitude: res.longitude, //经度
								iconPath: '/static/location.png', //显示的图标        
								rotate: 0, // 旋转度数
								width: 20, //宽
								height: 20, //高
								title: '你在哪了', //标注点名
								alpha: 0.5, //透明度
								callout: { //自定义标记点上方的气泡窗口 点击有效  
									content: '当前位置', //文本
									color: '#ffffff', //文字颜色
									fontSize: 14, //文本大小
									borderRadius: 2, //边框圆角
									bgColor: '#00c16f', //背景颜色
									display: 'ALWAYS' //常显
								}

							}];
							_this.circles = [{ //在地图上显示圆
								latitude: res.latitude, //纬度
								longitude: res.longitude, //经度
								radius: 50, //半径
								fillColor: "#87CEFA", //填充颜色
								color: "#55aaffAA", //描边的颜色
								strokeWidth: 1 //描边的宽度
							}]
						}
					},
					fail: function(res) {
						console.log('当前位置的经度');
					}
				});
			}
		}
	}
</script>

<style>
</style>
