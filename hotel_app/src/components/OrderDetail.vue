<template>
    <div id="orderDetail">
      <mu-container class="container">
        <mu-row>
          <mu-appbar class="appBar" color="primary">
            <mu-icon value="account_balance_wallet" slot="left"></mu-icon>
            订单详情
          </mu-appbar>
        </mu-row>
        <mu-row>
          <mu-col>
            <mu-card class="card" raised>
              <mu-card-header :title="'订单号: '+  order.orderId " :sub-title="'预订方式：'+ order.orderType">
                <mu-avatar slot="avatar">
                  <mu-icon value="payment"></mu-icon>
                </mu-avatar>
              </mu-card-header>
              <mu-card-text>
                <mu-list>
                  <mu-list-item>
                    <mu-list-item-action><mu-icon value="hotel"></mu-icon></mu-list-item-action>
                    <mu-list-item-title>房间类型： {{ order.roomType }}</mu-list-item-title>
                  </mu-list-item>
                  <mu-list-item v-show="order.orderStatus === 2">
                    <mu-list-item-action><mu-icon value="hotel"></mu-icon></mu-list-item-action>
                    <mu-list-item-title>房间号码： 102</mu-list-item-title>
                  </mu-list-item>
                  <mu-list-item>
                    <mu-list-item-action><mu-icon value="today"></mu-icon></mu-list-item-action>
                    <mu-list-item-title>预订日期:{{ order.orderDate | formatDate }}到{{ order.orderDate | addDate(order.orderDays) }}</mu-list-item-title>
                  </mu-list-item>
                  <mu-list-item>
                    <mu-list-item-action><mu-icon value="phone"></mu-icon></mu-list-item-action>
                    <mu-list-item-title>预订天数： {{ order.orderDays }}</mu-list-item-title>
                  </mu-list-item>
                  <mu-list-item>
                    <mu-list-item-action><mu-icon value="fingerprint"></mu-icon></mu-list-item-action>
                    <mu-list-item-title>入住人： {{ order.name }}</mu-list-item-title>
                  </mu-list-item>
                  <mu-list-item>
                    <mu-list-item-action><mu-icon value="phone"></mu-icon></mu-list-item-action>
                      <mu-list-item-title>联系手机： {{ order.phone }}</mu-list-item-title>
                  </mu-list-item>
                  <mu-list-item>
                    <mu-list-item-action><mu-icon value="today"></mu-icon></mu-list-item-action>
                    <mu-list-item-title>创建时间:{{ dateFormat(order.createTime) }}</mu-list-item-title>
                  </mu-list-item>
                  <mu-list-item >
                    <mu-list-item-action><mu-icon value="today"></mu-icon></mu-list-item-action>
                    <mu-list-item-title>更新时间:{{ dateFormat(order.updateTime) }}</mu-list-item-title>
                  </mu-list-item>
                </mu-list>
              </mu-card-text>
              <mu-card-actions class="actions">
                <mu-button style="width: 100%" flat>订单消费：￥ {{ order.orderCost }}</mu-button>

                <mu-button style="width: 49%" flat :color="order.orderStatus|getStatusColor">订单状态：{{ order.orderStatus|getOrderStatus }}</mu-button>

                <mu-button color="primary" @click="openPass = true" v-show="order.orderStatus === 0">立即付款</mu-button>
                <mu-button flat @click="goBack()">返回</mu-button>
                <mu-button color="error" @click="openCancel = true" v-show="order.orderStatus >= 0 && order.orderStatus <2">取消订单</mu-button>
              </mu-card-actions>
            </mu-card>
          </mu-col>
        </mu-row>
      </mu-container>
      <mu-dialog title="正在付款" width="360" :open.sync="openPass" :loading="payLoading">
        <p>订单总计：￥ {{ order.orderCost }}</p>
        <mu-text-field label="Password" v-model="password" label-float error-text="请输入支付密码" type="password" icon="fingerprint"></mu-text-field><br/>
        <mu-button slot="actions" flat color="error" @click="openPass = false">取消</mu-button>
        <mu-button slot="actions" flat color="primary" @click="pay()">确认付款</mu-button>
      </mu-dialog>
      <mu-dialog title="取消订单" width="360" :open.sync="openCancel" :loading="cancelLoading">
        确认取消订单吗？
        <mu-button slot="actions" flat color="secondary" @click="openCancel = false">返回</mu-button>
        <mu-button slot="actions" flat color="primary" @click="cancelOrder()">确认取消</mu-button>
      </mu-dialog>
    </div>
</template>

<script>
  import {getOrderById, cancelOrder, payOrder} from "@/api/order";
  import { getUserInfo } from "@/api/user";

  export default {
        name: "OrderDetail",
      data(){
          return{
            password: null,
            openPass: false,
            openCancel: false,
            orderId: null,
            payLoading : false,
            cancelLoading: false,
            order: {
              orderId: null,
            },
            userInfo: {
              userId : null,
              username: null,
            },
          }
      },
      created: function () {
        this.fetchData()
      },
      methods:{
          goBack(){
            this.$router.back()
          },
          fetchData(){
            const id = this.resolveOrderId()
            if (id == null){
              this.$toast.error('未找到对应的订单')
              this.$router.back()
              return
            }
            this.orderId = id
            this.loadOrder()
            this.loadUserInfo()
          },
        loadOrder(){
          if (!this.orderId){
            return
          }
          getOrderById(this.orderId).then(res => {
            const data = res.data
            if (!data){
              this.$toast.error('订单不存在或已被删除')
              return
            }
            this.order = data
          }).catch(err => {
            this.$toast.error(err.toString())
          })
        },
        loadUserInfo(){
          getUserInfo().then(res => {
            this.userInfo = res.data || {}
          }).catch(err => {
            this.$toast.error(err.toString())
          })
        },
        resolveOrderId(){
          const fromParams = this.$route.params && this.$route.params.orderId
          const fromQuery = this.$route.query && this.$route.query.orderId
          const candidate = fromParams || fromQuery
          if (!candidate){
            return null
          }
          const parsed = parseInt(candidate, 10)
          return Number.isNaN(parsed) ? null : parsed
        },
        getStaColor(val){
          var status = ''
          switch (val) {
            case -1:
              status = 'error'
              break;
            case 0:
              status = 'warning'
              status = 'red'
              break;
            case 1:
              status = 'success'
              break;
            case 2:
              status = 'primary'
              break;
            case 3:
              status = 'info'
              break;
          }
          return status
        },
        dateFormat(val){
          var d = new Date(val)
          var year = d.getFullYear()
          var month = d.getMonth() + 1
          var day = d.getDate() < 10 ? '0' + d.getDate() : '' + d.getDate()
          var hour = d.getHours();
          var minutes = d.getMinutes();
          var seconds = d.getSeconds();
          // return  year+ '-' + month + '-' + day + ' ' + hour + ':' + minutes + ':' + seconds;
          return year + '-' + month + '-' + day + '- ' + hour + ':' + minutes + ':' + seconds
        },
        pay(){
            if (!this.password) {
              this.$toast.warning('请输入支付密码')
              return
            }
            const username = this.userInfo && this.userInfo.username
            if (!username) {
              this.$toast.error('请重新登录后再试')
              return
            }
            this.payLoading = true
          payOrder(this.orderId,username,this.password).then(res => {
                if (res.code === 1000){
                  this.$toast.success(res.message || "支付成功！")
                  this.openPass = false
                  this.password = null
                  this.loadOrder()
                }else {
                  this.$toast.error(res.message || "支付失败！")
                }
              }).catch(err => {
                this.$toast.error(err.toString())
              }).finally(() => {
                this.payLoading = false
              })
        },
        cancelOrder(){
            this.cancelLoading = true
            cancelOrder(this.orderId).then(res => {
              if (res.code === 1000){
                this.$toast.success(res.message || "订单取消成功！")
                this.openCancel = false
                this.loadOrder()
              } else {
                this.$toast.info(res.message || "订单取消失败，请稍后再试！")
              }
            }).catch(err => {
              this.$toast.error("取消失败，原因："+ err.toString())
            }).finally(() => {
              this.cancelLoading = false
            })
        }
      }
    }
</script>

<style scoped>
  .container{
    padding: 10px;
  }
  .appBar{
    height: 40px;
    width: auto!important;
    border-radius: 35px;
    margin: 10px;
    color: #fff;
    padding: 10px;
  }
.card{
  padding: 10px;
  margin: 10px auto;
}
  .actions .mu-button{
    margin: 5px 0;
    width: 49%;
  }
</style>
