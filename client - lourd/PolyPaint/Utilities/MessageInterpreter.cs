using PolyPaint.Models;
using System.Collections.Generic;

namespace PolyPaint.Utilities
{
    class MessageInterpreter
    {
        public const string GameGuessPrefix = ".g";
        public const string GameCluePrefix = ".c";
        #region DROPPED FEATURE
        //public const string GameKickPrefix = ".k";
        #endregion

        private readonly List<string> prefixes = new List<string> { ".", ".h", GameGuessPrefix, GameCluePrefix };
        public bool IsHistoryRequest(string message)
        {
            return message.Equals(prefixes[1]);
        }
        public bool IsAGuess(string message)
        {
            return message.StartsWith(GameGuessPrefix);
        }
        public bool IsAClueRequest(string message)
        {
            return message.Equals(GameCluePrefix);
        }
        #region DROPPED FEATURE
        //public bool IsAKickRequest(string message)
        //{
        //    return message.StartsWith(GameKickPrefix);
        //}
        #endregion
        public string ExtractWordFromCommand(string message)
        {
            return message.Substring(2).Trim();
        }
        public bool IsACommand(string message)
        {
            return IsHistoryRequest(message)
                || (SharedChatModel.GameMode && (IsAGuess(message) || IsAClueRequest(message) /*|| message.StartsWith(GameKickPrefix)*/));
        }
    }
}
