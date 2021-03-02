interface DbParams {
    USER: string;
    PASSWORD: string;
    DB: string;
    HOST: string;
    PORT: string;
    URL: string;
    TEST_URL: string;
}

const dbParams: DbParams = {
    USER     : "Pi3MongoUser",
    PASSWORD : "Pi3MongoUsersPass",
    DB       : "Pi3Db?retryWrites=true&w=majority",
    HOST     : "cluster0-liqdc.mongodb.net",
    PORT     : "29484",
    URL      : "",
    TEST_URL : "",
};

dbParams.URL = "mongodb+srv://" + dbParams.USER + ":" + dbParams.PASSWORD + "@" + dbParams.HOST + "/" + dbParams.DB;
// admin pass: 2020IsTheZiggoulsYear

export { dbParams };
