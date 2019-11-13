package com.luv2code.aopdemo.aspect;

import com.luv2code.aopdemo.Account;
import com.luv2code.aopdemo.AroundWithLoggerDemoApp;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Aspect
@Component
@Order(2)
public class MyDemoLoggingAspect {


    private static Logger myLogger = Logger.getLogger(MyDemoLoggingAspect.class.getName());

    @Around("execution(* com.luv2code.aopdemo.service.*.getFortune(..))")
    public Object aroundGetFortune(ProceedingJoinPoint theProceedingJoinPoint) throws Throwable {
        String method = theProceedingJoinPoint.getSignature().toShortString();
        myLogger.info("\n======>>> Executing @Around on method: " + method);

        long begin = System.currentTimeMillis();
        Object result = theProceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        long durantion = end - begin;
        myLogger.info("\n======>>> Duration: " + durantion / 1000.0 + " seconds");
        return result;
    }

    @After("execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))")
    public void afterFinallyFindAccountsAdvice(JoinPoint theJoinPoint) {
        String method = theJoinPoint.getSignature().toShortString();
        myLogger.info("\n======>>> Executing @After (finally) on method: " + method);
    }

    @AfterThrowing(pointcut = "execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))", throwing = "theExc")
    public void afterThrowingFindAccountsAdvice(JoinPoint theJoinPoint, Throwable theExc) {
        String method = theJoinPoint.getSignature().toShortString();
        myLogger.info("\n======>>> Executing @AfterThrowing on method: " + method);
        myLogger.info("\n======>>> The exception is: " + theExc);
    }

    @AfterReturning(pointcut = "execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))", returning = "result")
    public void afterReturningFindAccountsAdvice(JoinPoint theJoinPoint, List<Account> result) {

        String method = theJoinPoint.getSignature().toShortString();
        myLogger.info("\n======>>> Executing @AfterReturning on method: " + method);
        myLogger.info("\n======>>> result is: " + result);
        convertAccountNamesToUpperCase(result);
        myLogger.info("\n======>>> result is: " + result);

    }

    private void convertAccountNamesToUpperCase(List<Account> result) {
        for (Account account : result) {

            String upperName = account.getName().toUpperCase();
            account.setName(upperName);

        }

    }

    @Before("com.luv2code.aopdemo.aspect.LuvAopExpressions.forDaoPackageNoGetterSetter()")
    public void beforeAddAccountAdvice(JoinPoint theJoinPoint) {
        myLogger.info("\n======>>> Executing @Before advice on method()");

        MethodSignature methodSignature = (MethodSignature) theJoinPoint.getSignature();
        myLogger.info("Method: " + methodSignature);

        Object[] args = theJoinPoint.getArgs();
        for (Object arg : args) {
            myLogger.info(arg.toString());
            if (arg instanceof Account) {
                Account account = (Account) arg;
                myLogger.info("Account name: " + account.getName());
                myLogger.info("Account level: " + account.getLevel());
            }
        }
    }
}