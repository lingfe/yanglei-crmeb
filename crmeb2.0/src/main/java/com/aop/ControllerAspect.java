package com.aop;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson.JSONObject;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.system.model.SystemLogs;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemLogsService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


/**
 * 所有的前端controller层的拦截业务！
 * 方法的执行时间长度！
 * https://blog.csdn.net/zhangsweet1991/article/details/83859026
 * https://blog.51cto.com/u_3631118/3121350
 * https://blog.csdn.net/hjw0505521/article/details/88623897?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7Edefault-1-88623897-blog-109821662.pc_relevant_default&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7Edefault-1-88623897-blog-109821662.pc_relevant_default&utm_relevant_index=2
 */
@Aspect
@Component
public class ControllerAspect {

    Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private SystemLogsService systemLogsService;

    /** 从该注解的位置切入 */
    @Pointcut("@annotation(io.swagger.annotations.ApiOperation)")
    private void pointCutMethodController() { }

    /** 执行前后 */
    @Around("pointCutMethodController()")
    public Object doAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        //开始执行时间
        long start = System.currentTimeMillis();

        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();//获取请求的类名
        Method method = signature.getMethod();//获取切入点所在的方法
        String methodName = method.getName(); //获取方法名

        //请求的参数
        Object[] args = joinPoint.getArgs();
        String params = this.getParamsToJSONStr(args,true);

        //获取操作类型和描述
        //验证是否涉及敏感信息
        ApiOperation log = method.getAnnotation(ApiOperation.class);
        String describe="";
        String operationType = "";
        if (log != null) {
            describe = log.value()!=null? log.value():"未知";
            operationType = log.notes()!= null?log.notes():"未知";
        }

        //创建sysLog对象
        SystemLogs sysLog = new SystemLogs();
        sysLog.setUid(userService.getUserId());
        Integer adminId = 0; try{ adminId = systemAdminService.getAdminId();}catch (Exception e){}
        sysLog.setAdminId(adminId);
        sysLog.setUrl(request.getRequestURI() ==null ? "" :request.getRequestURI());
        sysLog.setIp(CrmebUtil.getIpAddress(request));
        sysLog.setOperationMethod(className + "." + methodName);
        sysLog.setOperationDesc(describe);
        sysLog.setOperationType(operationType);
        sysLog.setParameter(params);
        sysLog.setOperationTime(DateUtil.nowDateTimeStr());
        String userAgentStr = request.getHeader("User-Agent");
        sysLog.setUserAgent(userAgentStr);
        sysLog.setServerAddress(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort());
        if(userAgentStr != null){
            UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
            if(userAgent !=null){
                sysLog.setDeviceName(userAgent.getOs().getName());
                sysLog.setBrowserName(userAgent.getBrowser().getName());
            }
        }

        //返回数据
        Object obj = null;
        try{
            //执行过程
            obj = joinPoint.proceed();
            if(obj!=null)sysLog.setReturnData(JSONObject.toJSONString(obj));
            sysLog.setLogType((byte)1);
            return obj;
        }catch (Exception e){
            if(e!=null){
                e.printStackTrace();
                String msg=e.getMessage() == null?"空指针异常":e.getMessage();
                sysLog.setErrorLogMsg(msg);
                sysLog.setLogType((byte)2);
                throw new CrmebException(msg);
            }else{
                throw new CrmebException("空指针异常");
            }
        }finally {
            //计算耗时
            long end =System.currentTimeMillis();
            Long timeConsuming = end - start;
            sysLog.setTimeConsuming(timeConsuming.intValue());

            //输入日志
            logger.info("【操作记录】用户({})或者管理者({}),在:{},操作了方法:{},耗时:{}ms",
                    sysLog.getUid(),
                    sysLog.getAdminId(),
                    sysLog.getOperationTime(),
                    sysLog.getOperationMethod(),
                    sysLog.getTimeConsuming());
            logger.info(sysLog.toString());
            systemLogsService.save(sysLog);
        }
    }

    /**
     * 获取请求入参
     * @param args 参数数组
     * @param assignConvertJsonException 是否在参数转换json失败时将失败异常赋值给返回变量
     * @return
     */
    private String getParamsToJSONStr(Object[] args,boolean assignConvertJsonException){
        String params = "";
        int argsLength = args.length;
        Object argumentObj=null;
        if(argsLength==1){
            argumentObj = args[0];
            if (argumentObj instanceof ServletRequest || argumentObj instanceof ServletResponse || argumentObj instanceof MultipartFile || argumentObj instanceof MultipartFile[]) {
                argumentObj = null;
            }
        }
        if(argsLength > 1){
            Object[] arguments  = new Object[argsLength];
            for (int i = 0; i < argsLength; i++) {
                if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof MultipartFile || args[i] instanceof MultipartFile[]) {
                    //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                    //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                    continue;
                }
                arguments[i] = args[i];
            }
            argumentObj = arguments;
        }
        //将参数转换成json
        if (argumentObj != null) {
            try {
                params = JSONObject.toJSONString(argumentObj);
            } catch (Exception e) {
                if(assignConvertJsonException){
                    params = "入参转换至JSON异常:"+e.toString();
                }
            }
        }
        return params;
    }

}
