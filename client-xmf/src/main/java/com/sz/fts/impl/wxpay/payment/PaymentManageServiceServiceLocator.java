/**
 * PaymentManageServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Aug 08, 2005 (11:49:10 PDT) WSDL2Java emitter.
 */

package com.sz.fts.impl.wxpay.payment;

public class PaymentManageServiceServiceLocator extends org.apache.axis.client.Service implements PaymentManageServiceService {

    public PaymentManageServiceServiceLocator() {
    }


    public PaymentManageServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PaymentManageServiceServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PaymentManageService
    private String PaymentManageService_address = "http://qiaomin-PC:7001/webservice/services/PaymentManageService";

    public String getPaymentManageServiceAddress() {
        return PaymentManageService_address;
    }

    // The WSDD service name defaults to the port name.
    private String PaymentManageServiceWSDDServiceName = "PaymentManageService";

    public String getPaymentManageServiceWSDDServiceName() {
        return PaymentManageServiceWSDDServiceName;
    }

    public void setPaymentManageServiceWSDDServiceName(String name) {
        PaymentManageServiceWSDDServiceName = name;
    }

    public PaymentManageService getPaymentManageService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PaymentManageService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPaymentManageService(endpoint);
    }

    public PaymentManageService getPaymentManageService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            PaymentManageServiceSoapBindingStub _stub = new PaymentManageServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getPaymentManageServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPaymentManageServiceEndpointAddress(String address) {
        PaymentManageService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (PaymentManageService.class.isAssignableFrom(serviceEndpointInterface)) {
            	PaymentManageServiceSoapBindingStub _stub = new PaymentManageServiceSoapBindingStub(new java.net.URL(PaymentManageService_address), this);
                _stub.setPortName(getPaymentManageServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("PaymentManageService".equals(inputPortName)) {
            return getPaymentManageService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://payment", "PaymentManageServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://payment", "PaymentManageService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("PaymentManageService".equals(portName)) {
            setPaymentManageServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
