package com.sz.fts.utils.liuliang;

public class NewOrderConfig {
    public final static String CHARSET = "UTF-8";

    public final static String ALGORITHM = "SHA1withRSA";

    /**
     * 公钥
     */
    public static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSkPZHdQBsk4MlDNv3Uybq49sTnJ2GGN8nknnuUz6K4rZRWA28Pj5pNAbOZtln4kToseHugTAkPkjx1MQ0cDf0duxxw+UtqR7cxaQQIVXxgXieO+FyVqWvr2t1oMb72g7uAQvFOXlbI5EBLkQXe+azXc8UTIgDLZsGpokSG3ipjQIDAQAB";

    /**
     * 私钥
     */
    public static String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJKQ9kd1AGyTgyUM2/dTJurj2xOcnYYY3yeSee5TPoritlFYDbw+Pmk0Bs5m2WfiROix4e6BMCQ+SPHUxDRwN/R27HHD5S2pHtzFpBAhVfGBeJ474XJWpa+va3WgxvvaDu4BC8U5eVsjkQEuRBd75rNdzxRMiAMtmwamiRIbeKmNAgMBAAECgYAUio6Jq0M49F+f9LJrclL0/qQ4lm2ZWqkD4cqG9VHBo06MDfw20r5qfikqCk4T+ilQN4YDqEV3/ua4ReqNsuCUgz5E81Q+4qQKFALfHIrHO7tJyIteGQhWyw6fhzmOe+kUVGWCd2zccqw71Mnmt6kap+4M4FCmxBmOwCVGCkxavQJBAMMRugsbGUOgyghZ2potlzr3ra6uMSZxO9fFHBL631l1GPfxwbTENIHoGvnZLcGrSwbyamPI0SLoM82XlaqaZqsCQQDAWM2VlQi/04Hcnm71q9tVkVBSo07j0yFGn2S8D8qhchvJwQ04MO6RJAyqtjxSsb3DvIFfsBY7ocU5/cWy/xCnAkEAhIiTvZ3wwVkKSzg5ZQ1ata6363ngXP0MKqJ6W47llxMPHs67zESOZEC9q0laZD4sYJDtpdQ3+56GK8Y04s+6twJAAIE7KOVXPImlkZqo1+Q3kyXqfA0Jq/dl4vWQIh44fev6vrDPBBK8zYy2q8wGpIDq6pSYElQ3+rgV+Gz+E9YviQJACtvoBzVo+Vdv7eJ/nzCmsN9WE6QsLgefiQf5yFebeSgsWTjEY00FlcXoBl3X1x1okSijWS8V9bnVP/qv0JlXbg==";

    /**
     * 受理业务前的业务受理校验，主要是进行校验用户是否可受理此业务。订购接口取该接口出参中body- bodys中的trandId 作为订购接口的入参.
     */
    public final static String BSS_URL_ORDER_CHECK = "http://61.160.184.18:8081/api-distri/v1/business/check";

    /**
     * 根据业务受理提供的受理流水号进行业务的受理。
     */
    public final static String BSS_URL_ORDER_ACCEPTANCE = "http://61.160.184.18:8081/api-distri/v1/business/acceptance";

    /**
     * 查询已经订购的所有业务，包含基础业务、集团、增值业务和快消品业务
     */
    public final static String BSS_URL_ORDER_QUERY = "http://61.160.184.18:8081/api-distri/v1/business/query";

    /**
     * appId
     */
    public static final String appId = "93272393";

    /**
     * 受理工号
     */
    public static final String staffCode = "JSDQLL_SZCD";

    /**
     * 受理渠道
     */
    public static final String channelId = "3205002257619";
    
}
