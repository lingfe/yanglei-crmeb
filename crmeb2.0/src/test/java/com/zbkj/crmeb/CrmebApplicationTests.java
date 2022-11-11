package com.zbkj.crmeb;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration
public class CrmebApplicationTests {


    //@Autowired
    //private UserService userService;
    //
    //@Test
    //public void test(){
    //    Boolean bl= userService.passwordUpdate("18085128290","123212","123456",2);
    //    System.out.println(Integer.parseInt(null));
    //}


    //@Autowired
    //private StoreProductReplyService storeProductReplyService;
    //
    //public static void main(String[] args) {
    //    System.out.println(Integer.parseInt(null));
    //}
    //
    //// 添加虚拟评论
    //@Test
    //public void test(){
    //    String path="C:\\Users\\Administrator\\Desktop\\Aqykj\\评价数据模板1.xlsx";
    //
    //    //读-hutool
    //    ExcelReader reader = ExcelUtil.getReader(path);
    //    List<Map<String, Object>> listMap =  reader.readAll();
    //    StoreProductReply storeProductReply=null;
    //    List<StoreProductReply> storeProductReplyArrayList=new ArrayList<>();
    //    for (Map<String,Object> m:listMap) {
    //        storeProductReply=new StoreProductReply();
    //        storeProductReply.setProductId(this.get(String.valueOf(m.get("商品ID"))));
    //        storeProductReply.setServiceScore(this.get(String.valueOf(m.get("服务分"))));
    //        storeProductReply.setProductScore(this.get(String.valueOf(m.get("商品分"))));
    //        storeProductReply.setComment(String.valueOf(m.get("评价内容")));
    //        storeProductReply.setPics(String.valueOf(m.get("评价图片")));
    //        storeProductReply.setAvatar(String.valueOf(m.get("用户头像")));
    //        storeProductReply.setNickname(String.valueOf(m.get("用户名称")));
    //        storeProductReply.setSku(String.valueOf(m.get("购买规格(瓶500mL)")));
    //        storeProductReplyArrayList.add(storeProductReply);
    //    }
    //
    //    //执行保存
    //    storeProductReplyService.saveBatch(storeProductReplyArrayList);
    //}
    //
    //public Integer get(String obj){return obj == null?0:Integer.parseInt(obj);}

//    @Test
//    void contextLoads() {
//    }

//    @Autowired
//    private UserMapper userMapper;
//
//    @Test
//    void testSelecte(){
//        System.out.println("----------Start test----------");
//        List<User> userList = userMapper.selectList(null);
//        Assert.assertEquals(5, userList.size());
//        userList.forEach(System.out::println);
//    }

    //@Autowired
    //private TemplateMessageService templateMessageService;
    //
    //@Autowired
    //private UserTokenService userTokenService;

    //@Test
    //public void testmsg(){
    //    // 小程序发送订阅消息
    //    UserToken userToken = userTokenService.getTokenByUserId(57, UserConstants.USER_TOKEN_TYPE_ROUTINE);
    //    if (!ObjectUtil.isNull(userToken)) {
    //        // 组装数据
    //        HashMap<String, String> temMap=new HashMap();
    //        temMap.put("number8", "200");
    //        temMap.put("character_string12", "1500");
    //        temMap.put("date5", DateUtil.nowDateTime(Constants.DATE_FORMAT));
    //        temMap.put("thing10", "您的积分已到帐！");
    //        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_INTEGAL_TONZHI, temMap, userToken.getToken());
    //    }else{
    //        System.out.println("积分支付订单分账-小程序消息发送失败!");
    //    }
    //}

//    @Autowired
//    private SmsService smsService;
//
//    @Autowired
//    private StoreProductService storeProductService;
//
////    @Autowired
////    private UserExtractService userExtractService;
//
////    @Autowired
////    private CloudAccountService cloudAccountService;
//
//
//    @Autowired
//    private SplitAccountService splitAccountService;
//
//    //@Test
//    //public void payProfitSharingAlipayAddReceiver(){
//    //    splitAccountService.payProfitSharingAlipayAddReceiver(100);
//    //}
//
//    @Autowired
//    private BusinessTypeService businessTypeService;

    // 导入分类
    //@Test
    //public void addBusinessType(){
    //    //String path="C:\\Users\\Administrator\\Desktop\\Aqykj\\产品\\date\\2021-08-20\\-3\\2021-08-20\\proinfo.xlsx";
    //    String path="C:\\Users\\Administrator\\Desktop\\Aqykj\\产品\\butype.xlsx";
    //    //读-hutool
    //    ExcelReader reader = null;
    //    reader = ExcelUtil.getReader(path);
    //    Map<String,Object> map = new HashMap<>();
    //    List<Map<String, Object>> listMap =  reader.readAll();
    //    Integer pid2 = 0;
    //    int tt=1;
    //    Boolean is=false;
    //    BusinessType pidType=new BusinessType();
    //    List<BusinessType> businessTypeList=new ArrayList<>();
    //    for (Map<String,Object> m:listMap) {
    //        //根据名称读取
    //        String title=m.get("类型").toString();
    //
    //        //验证-1级
    //        if(!title.equals(map.get("类型"))){
    //            map = m;
    //            is=true;
    //
    //            //赋值
    //            BusinessType type=new BusinessType();
    //            type.setId(tt);
    //            type.setTitle(title);
    //            type.setPid(0);
    //            pidType = type;
    //            //添加
    //            businessTypeList.add(type);
    //            tt++;
    //        }else{
    //            is=false;
    //        }
    //
    //        //验证2级
    //        String title2=map.get("2级分类").toString();
    //        if(is||!title2.equals(m.get("2级分类"))){
    //            map = m;
    //
    //            //赋值
    //            BusinessType type=new BusinessType();
    //            type.setId(tt);
    //            type.setTitle(m.get("2级分类").toString());
    //            type.setPid(pidType.getId());
    //            pid2=type.getId();
    //            //添加
    //            businessTypeList.add(type);
    //            tt++;
    //        }
    //
    //        //3级
    //        BusinessType type=new BusinessType();
    //        type.setId(tt);
    //        type.setTitle(m.get("3级分类").toString());
    //        type.setPid(pid2);
    //        //添加
    //        businessTypeList.add(type);
    //        tt++;
    //    }
    //
    //    //添加-到数据库
    //    businessTypeService.saveBatch(businessTypeList);
    //    //String path="C:\\Users\\Administrator\\Desktop\\Aqykj\\产品\\date\\test.xlsx";
    //    //StoreProductExcelResponse rimplesponse= storeProductService.importProductExcelUpgrade(path);
    //    System.out.println(map);
    //}

//    @Test
//    public void queryAccounts()   {
//        //String path="C:\\Users\\Administrator\\Desktop\\Aqykj\\产品\\date\\2021-08-20\\-3\\2021-08-20\\proinfo.xlsx";
//        String path="C:\\Users\\Administrator\\Desktop\\Aqykj\\产品\\date\\2021-09-14\\SB.xlsx";
//        //String path="C:\\Users\\Administrator\\Desktop\\Aqykj\\产品\\date\\test.xlsx";
//        StoreProductExcelResponse rimplesponse= storeProductService.importProductExcelUpgrade(path);
//        System.out.println(rimplesponse);
//    }

//    @Test
//    public void queryAccounts()   {
//        DayStreamDataListResponse list = cloudAccountService.queryDayStream("2021-08-17");
//        System.out.println(list);
//    }


//    @Test
//    public void queryAccounts()   {
//        DealerBalanceDetailResponse dealerBalanceDetailResponse = cloudAccountService.queryAccounts();
//        System.out.println(dealerBalanceDetailResponse);
//    }

//    @Test
//    public void assembleParam() throws Exception {
//        System.out.println("1111111111111");
//        userExtractService.applyWithdrawalToAlipay(new UserExtractRequest().setExtractPrice(new BigDecimal(0.1)));
//    }
//
//    @Test
//    public void testIsLogin() {//是否登录
////        JSONObject login = smsService.isLogin();
////        System.out.println(login);
//    }
//
//    @Test
//    public void testSendCodeForRegister() {//注册短信
//
//    }
//
//    @Test
//    public void testLogin() {//登录
//
//    }
//
//    @Test
//    public void testInfo() {//用户信息
//
//    }
//
//    @Test
//    public void testTempList() {//短信模板列表
//
//    }
//
//    @Test
//    public void testPayList() {//支付套餐列表
//
//    }
//
//    @Test
//    public void testPayQrcode() {//支付码
//
//    }
//
//    @Test
//    public void sendCode() {//发送短信
//
//    }
}
