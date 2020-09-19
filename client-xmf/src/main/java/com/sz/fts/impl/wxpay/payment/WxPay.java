package com.sz.fts.impl.wxpay.payment;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WxPay {

	public String pay() {
		String wsdlUrl = "http://132.228.185.30:80/webservice/services/PaymentManageService?wsdl";
		String TimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        StringBuffer inParameter = new StringBuffer();
        String TransactionID = TimeStamp+"123456";
        String transationType= "Pay";
        String SpId ="1001";
        String orderType ="12";
        String fee = "1";
        String orderSeq =SpId + TimeStamp + "123456";//SPID+YYYYMMDDHHMISS+6
        String payInterfaceType ="wxpay";
        String riskControlInfo = "{\"service_identify\":\""+"10000000"+"\",\"subject\":\""+"测试的钢笔"+
            	"\",\"product_type\":\""+"1"+"\",\"body\":\""+"很好用的钢笔，英雄"+"\",\"goods_count\":\""+"2"+"\",\"service_cardno\":\""+"111"+"\"}";
        
        inParameter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        inParameter.append("<Payment>");
        inParameter.append("<TransactionID>"+TransactionID+"</TransactionID>");
        inParameter.append("<TransactionType>"+transationType+"</TransactionType>");
        inParameter.append("<TimeStamp>" + TimeStamp + "</TimeStamp>");
        inParameter.append("<PayInterfaceType>"+payInterfaceType+"</PayInterfaceType>");//
        inParameter.append("<SPID>"+SpId+"</SPID>");
        inParameter.append("<OrderType>"+orderType+"</OrderType>"); //12:
        inParameter.append("<CurrencyType>01</CurrencyType>");//01:
        inParameter.append("<Fee>"+fee+"</Fee>"); //
        inParameter.append("<ClientIP>127.0.0.1</ClientIP>");
        inParameter.append("<OrderSeq>"+orderSeq+"</OrderSeq>");
        inParameter.append("<tradeType>NATIVE</tradeType>");//
        inParameter.append("<appidTag>1</appidTag>");
        inParameter.append("<PayGoodsType>"+riskControlInfo+"</PayGoodsType>");
        inParameter.append("</Payment>");

        System.out.println("JS_UNI_PAY入参:["+inParameter+"]");
        
        PaymentManageServiceServiceLocator locator = new PaymentManageServiceServiceLocator();
        locator.setPaymentManageServiceEndpointAddress(wsdlUrl);
        PaymentManageServiceSoapBindingStub binding = null;
        try {
            binding = (PaymentManageServiceSoapBindingStub) locator.getPaymentManageService();
        } catch (ServiceException ex1) {
        }
        binding.setTimeout(60000);
        try {
            String xx = binding.doKeyPayment(inParameter.toString());
            System.out.println("JS_UNI_PAY回参:["+xx+"]");
            return xx;
        } catch (RemoteException ex) {
        	System.out.println(ex);
        	return "";
        }

    }
	
	public static void main(String arg[]){
		WxPay wp = new WxPay();
		wp.pay();
	}
}
