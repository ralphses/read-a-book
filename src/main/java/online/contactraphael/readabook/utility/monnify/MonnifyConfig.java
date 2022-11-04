package online.contactraphael.readabook.utility.monnify;

public class MonnifyConfig {

    public static final String[] ALLOWED_PAYMENT_METHODS = {"CARD","ACCOUNT_TRANSFER"};
    public static final String AUTHORIZATION_PREFIX = "Bearer ";
    public static final String BASIC_AUTHORIZATION_PREFIX = "Basic ";

    public static final String CONTRACT_CODE = "0840919684";
    public static final String NGN_CURRENCY_CODE = "NGN";
    public static final String WEMA_BANK_CODE = "035";

    public static final String MONNIFY_BASE_URL = "https://sandbox.monnify.com";
    public static final String LOGIN_URL = "/api/v1/auth/login";
    public static final String INIT_TRANSACTION_URL = "/api/v1/merchant/transactions/init-transaction";
    public static final String TRANSACTION_STATUS_URL = "/api/v2/transactions/";
    public static final String GET_BANKS_URL = "/api/v1/sdk/transactions/banks";
    public static final String PAY_WITH_CARD_URL = "/api/v1/merchant/cards/charge";


}
