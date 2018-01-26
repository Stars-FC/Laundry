package com.wingfac.laundry.api;


import com.wingfac.laundry.bean.AddressBean;
import com.wingfac.laundry.bean.AppointmentConfirmOrderBean;
import com.wingfac.laundry.bean.CanCloseBean;
import com.wingfac.laundry.bean.CloseBean;
import com.wingfac.laundry.bean.CommercialMoneyBean;
import com.wingfac.laundry.bean.CommercialOrderBean;
import com.wingfac.laundry.bean.CommodityBean;
import com.wingfac.laundry.bean.HeadImgBean;
import com.wingfac.laundry.bean.MessageBean;
import com.wingfac.laundry.bean.NearbyCloseBean;
import com.wingfac.laundry.bean.OneCommodityBean;
import com.wingfac.laundry.bean.PayBean;
import com.wingfac.laundry.bean.RechargeHistoryBean;
import com.wingfac.laundry.bean.RecommendBean;
import com.wingfac.laundry.bean.ShopHomeBean;
import com.wingfac.laundry.bean.ShopOrderBean;
import com.wingfac.laundry.bean.ShopOrderListBean;
import com.wingfac.laundry.bean.ShoppingLogisticsBean;
import com.wingfac.laundry.bean.StoreBean;
import com.wingfac.laundry.bean.StoreClassBean;
import com.wingfac.laundry.bean.StoreDetailBean;
import com.wingfac.laundry.bean.TitleClassBean;
import com.wingfac.laundry.bean.TwoClassBean;
import com.wingfac.laundry.bean.UserBean;
import com.wingfac.laundry.bean.WashDetailBean;
import com.wingfac.laundry.bean.WashMessageBean;
import com.wingfac.laundry.bean.WashOrderListBean;
import com.wingfac.laundry.bean.base.Base;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public interface APPService {
    @Multipart
    @POST("LaundryMall/Login_Registration/registered.action")
    Observable<Base> register(
            @Part("auMobile") RequestBody mobile,
            @Part("code") RequestBody passwordOne,
            @Part("auPassword") RequestBody nick,
            @Part("payPassword") RequestBody payPassword,
            @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/validateLogon.action")
    Observable<UserBean> login(@Field("auMobile") String mobile, @Field("auPassword") String password);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/modifyPassword.action")
    Observable<Base> forget(@Field("auMobile") String mobile, @Field("code") String code, @Field("auPassword") String auPassword);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/sendOutYZM.action")
    Observable<Base> sendCode(@Field("phone") String phone, @Field("judge") String judge);

    @FormUrlEncoded
    @POST("LaundryMall/Advertisement/displayAd.action")
    Observable<HeadImgBean> headImg(@Field("levels") String levels);

    @GET("LaundryMall/Advertisement/getSystemData.action")
    Observable<HeadImgBean> getHomeImg();

    @POST("LaundryMall/Header_Management/showLevelHeadingsAPP.action")
    Observable<TitleClassBean> getOneClass();

    @FormUrlEncoded
    @POST("LaundryMall/Header_Management/showTwoTitleByFirstLevelTitle.action")
    Observable<TwoClassBean> getTwoClass(@Field("lmId") String lmId);

    @POST("LaundryMall/Recommended_Categories/showRecommended.action")
    Observable<RecommendBean> recommend();

    @FormUrlEncoded
    @POST("LaundryMall/Shop_Recommendation/showAllRecommendedStores.action")
    Observable<StoreBean> getOneAllStore(@Field("pstart") String pstart, @Field("psize") String psize, @Field("level") String level);

    @FormUrlEncoded
    @POST("LaundryMall/Shop_Recommendation/displayAccordingToLevelAndRecommendationCategory.action")
    Observable<StoreBean> getRecommendStore(@Field("s_level") String s_level, @Field("rc_id") String rc_id, @Field("pstart") String pstart, @Field("psize") String psize);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Mobile/displayStoreInformationThroughStoreID.action")
    Observable<StoreDetailBean> getStoreDetail(@Field("s_id") String s_id);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Mobile/shopNameFuzzyQuery.action")
    Observable<StoreBean> getForWord(@Field("s_name") String s_name, @Field("pstart") String pstart, @Field("psize") String psize);

    @FormUrlEncoded
    @POST("LaundryMall/Commodity/fuzzyQueryCommodity.action")
    Observable<CommodityBean> getCommentForWord(@Field("c_name") String c_name, @Field("pstart") String pstart, @Field("psize") String psize);

    @Multipart
    @POST("LaundryMall/Login_Registration/modifyUserInformation.action")
    Observable<UserBean> upDataUser(
            @Part("auId") RequestBody auId,
            @Part("auMobile") RequestBody auMobile,
            @Part("password") RequestBody password,
            @Part("nickname") RequestBody nickname,
            @Part("payPassword") RequestBody payPassword,
            @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("LaundryMall/Commodity/displayStoreMerchandise.action")
    Observable<ShopHomeBean> getShopHome(@Field("s_id") String s_id);

    @FormUrlEncoded
    @POST("LaundryMall/Commodity/viewCategoryItems.action")
    Observable<CommodityBean> getCommdityList(@Field("s_id") String s_id, @Field("cc_id") String cc_id);

    @FormUrlEncoded
    @POST("LaundryMall/Commodity/viewCommodityInformation.action")
    Observable<OneCommodityBean> getCommend(@Field("c_id") String c_id);

    @FormUrlEncoded
    @POST("LaundryMall/Shopping_Cart/newShoppingCartInformation.action")
    Observable<Base> addCart(@Field("auId") String auId, @Field("c_id") String c_id);

    @FormUrlEncoded
    @POST("LaundryMall/Shopping_Cart/displayShoppingCartInformation.action")
    Observable<CommodityBean> getCart(@Field("auId") String auId, @Field("pstart") String pstart, @Field("psize") String psize);

    @FormUrlEncoded
    @POST("LaundryMall/Shopping_Cart/batchDelete.action")
    Observable<Base> removeCar(@Field("auId") String auId, @Field("scId") String scId);

    @FormUrlEncoded
    @POST("LaundryMall/User_Message/viewSystemMessages.action")
    Observable<MessageBean> getMessage(@Field("auId") String auId, @Field("type") String type, @Field("category") String category, @Field("read_state") String read_state);

    @FormUrlEncoded
    @POST("LaundryMall/User_Message/removalMessage.action")
    Observable<Base> removeMessage(@Field("um_id") String um_id);

    @FormUrlEncoded
    @POST("wardrobe/Mobile_Phone_Terminal/showNearbyLaundryCabinet.do")
    Observable<NearbyCloseBean> getLocationClose(@Field("longitude") String longitude, @Field("latitude") String latitude);

    @FormUrlEncoded
    @POST("LaundryMall/Commodity/displayStoreMerchandise.action")
    Observable<ShopHomeBean> getWardrobeHome(@Field("s_id") String s_id);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/displayUserInformation.action")
    Observable<UserBean> getUser(@Field("auId") String auId);

    @FormUrlEncoded
    @POST("LaundryMall/Recharge_Status/displayRechargeInformation.action")
    Observable<RechargeHistoryBean> getRechargeHistory(@Field("uId") String uId,@Field("orderType") String orderType);

    @FormUrlEncoded
    @POST("LaundryMall/Recharge_Status/deleteRechargeInformation.action")
    Observable<Base> deleteRechargeHistory(@Field("boId") String boId);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/changePaymentPassword.action")
    Observable<Base> upDataPayPassword(@Field("auId") String auId, @Field("payPassword") String payPassword);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/modifyDefaults.action")
    Observable<Base> defaultAddress(@Field("auId") String auId, @Field("ua_id") String ua_id, @Field("ua_default") String ua_default);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/newUserAddress.action")
    Observable<Base> addAddress(@Field("auId") String auId, @Field("ua_name") String ua_name, @Field("ua_mobile") String ua_mobile, @Field("ua_address") String ua_address);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/modifyAddressInformation.action")
    Observable<Base> alertAddress(@Field("ua_id") String ua_id, @Field("ua_name") String ua_name, @Field("ua_mobile") String ua_mobile, @Field("ua_address") String ua_address);

    @FormUrlEncoded
    @POST("LaundryMall/Login_Registration/displayUserAddress.action")
    Observable<AddressBean> getAddress(@Field("auId") String auId);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Mobile/latitudeAndLongitudeClassificationViewShop.action")
    Observable<StoreBean> getShop(@Field("tlm_id") String tlm_id, @Field("s_longitude") String s_longitude, @Field("s_latitude") String s_latitude);

    @FormUrlEncoded
    @POST("wardrobe/Mobile_Phone_Terminal/viewCommodityInformation.do")
    Observable<CanCloseBean> canClose(@Field("longitude") String longitude, @Field("latitude") String latitude);

    @FormUrlEncoded
    @POST("wardrobe/Mobile_Phone_Terminal/sortView.do")
    Observable<CloseBean> getClose(@Field("category") String category, @Field("aId") String aId);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/newOrderInformation.action")
    Observable<ShopOrderBean> getOneOrder(@Field("auId") String auId, @Field("sId") String sId, @Field("cId") String cId, @Field("number") String number, @Field("total") String total);

    @Multipart
    @POST("LaundryMall/Store_Mobile/newStore.action")
    Observable<Base> addStore(
            @Part("id") RequestBody id,
            @Part("s_name") RequestBody s_name,
            @Part("open_time") RequestBody open_time,
            @Part("s_mobile") RequestBody s_mobile,
            @Part("s_address") RequestBody s_address,
            @Part("describe") RequestBody describe,
            @Part("s_longitude") RequestBody s_longitude,
            @Part("s_latitude") RequestBody s_latitude,
            @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("LaundryMall/Store_Mobile/modifyStoreInformation.action")
    Observable<Base> upDataStore(
            @Part("s_id") RequestBody s_id,
            @Part("s_name") RequestBody s_name,
            @Part("open_time") RequestBody open_time,
            @Part("s_mobile") RequestBody s_mobile,
            @Part("s_address") RequestBody s_address,
            @Part("describe") RequestBody describe,
            @Part("s_longitude") RequestBody s_longitude,
            @Part("s_latitude") RequestBody s_latitude,
            @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Mobile/modifyStoreAddress.action")
    Observable<Base> upDataStoreAddress(@Field("s_id") String s_id,
                                        @Field("s_address") String s_address,
                                        @Field("s_longitude") String s_longitude,
                                        @Field("s_latitude") String s_latitude);

    @Multipart
    @POST("LaundryMall/Store_Mobile/modifyShopAdvertisingMap.action")
    Observable<Base> addStoreFile(@Part("s_id") RequestBody s_id, @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("LaundryMall/Auditing_Goods/newAuditItems.action")
    Observable<Base> addCommodity(@Part("sId") RequestBody sId,
                                  @Part("ccId") RequestBody ccId,
                                  @Part("agName") RequestBody agName,
                                  @Part("unitPrice") RequestBody unitPrice,
                                  @Part("agStandard") RequestBody agStandard,
                                  @Part("agSize") RequestBody agSize,
                                  @Part("agUnit") RequestBody agUnit,
                                  @Part("agIntroduce") RequestBody agIntroduce,
                                  @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("LaundryMall/Commodity/ModifyCommodityInformation.action")
    Observable<Base> upDataCommodity(@Part("c_id") RequestBody c_id,
                                     @Part("c_name") RequestBody c_name,
                                     @Part("unit_price") RequestBody unit_price,
                                     @Part("c_standard") RequestBody c_standard,
                                     @Part("c_size") RequestBody c_size,
                                     @Part("c_unit") RequestBody c_unit,
                                     @Part("c_introduce") RequestBody c_introduce,
                                     @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Mobile/modifyStoreAddress.action")
    Observable<Base> getMoney(@Field("s_id") String s_id,
                              @Field("s_address") String s_address,
                              @Field("s_longitude") String s_longitude,
                              @Field("s_latitude") String s_latitude);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/viewShopOrder.action")
    Observable<CommercialOrderBean> getOrder(@Field("sId") String s_id,
                                             @Field("psize") String psize,
                                             @Field("pstart") String pstart);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/timeOrder.action")
    Observable<CommercialOrderBean> getOrderForTime(@Field("sId") String s_id,
                                                    @Field("strtime") String strtime,
                                                    @Field("endtime") String endtime,
                                                    @Field("psize") String s_address,
                                                    @Field("pstart") String s_longitude);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Mobile/enterTheShopAdvertisingChartInterface.action")
    Observable<StoreClassBean> getStoreClass(@Field("id") String id);

    @FormUrlEncoded
    @POST("LaundryMall/Commodity/displayAllGoods.action")
    Observable<CommodityBean> getAllGoods(@Field("id") String id, @Field("psize") String psize, @Field("pstart") String pstart);

    @Multipart
    @POST("LaundryMall/Commodity_Category/modifyCommodityCategories.action")
    Observable<Base> alertClass(@Part("cc_id") RequestBody cc_id, @Part("cc_name") RequestBody cc_name, @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/viewOrderLogisticsInformation.action")
    Observable<ShoppingLogisticsBean> getLogistic(@Field("soId") String s_id);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/viewOrderDetails.action")
    Observable<ShopOrderBean> getOrderDetail(@Field("soId") String soId);


    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/evaluateOrder.action")
    Observable<Base> addGood(@Field("soId") String s_id);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/viewUserOrders.action")
    Observable<ShopOrderListBean> getOrderList(@Field("auId") String auId, @Field("psize") String psize, @Field("pstart") String pstart, @Field("soState") String soState);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/bulkDeleteOrder.action")
    Observable<Base> removeOrder(@Field("soId") String soId);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/modifyOrderAddress.action")
    Observable<Base> setAddress(@Field("soId") String so_id, @Field("uaId") String uaId);

    @FormUrlEncoded
    @POST("wardrobe/Laundry_Order/userOrder.do")
    Observable<AppointmentConfirmOrderBean> getWashOrder(@Field("auId") String auId, @Field("cId") String cId, @Field("number") String number, @Field("eId") String eId, @Field("total") String total);

    @FormUrlEncoded
    @POST("wardrobe/Laundry_Order/modifyOrdermessage.do")
    Observable<Base> liuYan(@Field("loId") String loId, @Field("guestBook") String guestBook);

    @FormUrlEncoded
    @POST("wardrobe/Laundry_Order/viewOrderdetails.do")
    Observable<WashDetailBean> getWashOrderDetail(@Field("loId") String loId);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/payStoreOrder.action")
    Observable<PayBean> payStore(@Field("soId") String soId, @Field("auId") String auId, @Field("payType") String payType, @Field("fromType") String fromType);

    @FormUrlEncoded
    @POST("LaundryMall/Balance_Order_Controller/addUserBalance.action")
    Observable<PayBean> payWallet(@Field("uId") String uId, @Field("balance") String balance, @Field("payType") String payType, @Field("fromType") String fromType);

    @FormUrlEncoded
    @POST("wardrobe/Laundry_Order/payOrder.do")
    Observable<PayBean> payWash(@Field("loId") String loId, @Field("payType") String payType, @Field("oType") String oType, @Field("content") String content, @Field("fromType") String fromType);

    @FormUrlEncoded
    @POST("wardrobe/Laundry_Order/getLaudryOrderById.do")
    Observable<WashOrderListBean> getWashOrderList(@Field("uId") String uId, @Field("lo_state") String lo_state, @Field("page") String page, @Field("pageSize") String pageSize);

    @FormUrlEncoded
    @POST("wardrobe/Laundry_Order/getUnreciveOrderInfo.do")
    Observable<WashMessageBean> getWashMessage(@Field("page") String page, @Field("pageSize") String pageSize, @Field("uId") String uId);

    @FormUrlEncoded
    @POST("wardrobe/Laundry_Order/deleteLaundryOrder.do")
    Observable<Base> removeWashList(@Field("loId") String loId);

    @FormUrlEncoded
    @POST("LaundryMall/Store_Order/getStoreOrderRecord.action")
    Observable<CommercialMoneyBean> getMoney(@Field("uId") String uId,@Field("beginTime") String beginTime,@Field("endTime") String endTime);

    @FormUrlEncoded
    @POST("LaundryMall/Balance_Order_Controller/addUserBalanceOrder.action")
    Observable<Base> tiXian(@Field("uId") String uId,@Field("out_total_price") String out_total_price,@Field("payee_account") String payee_account
            ,@Field("pay_type") String pay_type
            ,@Field("payee_type") String payee_type);
}
