namespace PolyPaint.Models
{
    class LoginMessage
    {
        public string nickname { get; set; }
        public string password { get; set; }

        public LoginMessage(string nickname, string password)
        {
            this.nickname = nickname;
            this.password = password;
        }
    }
}
