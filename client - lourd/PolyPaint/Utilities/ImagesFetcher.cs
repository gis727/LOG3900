using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text.RegularExpressions;

namespace FMUD.Utilities
{
    class ImagesFetcher
    {
        private List<string> imagesLinks = new List<string>();
        private string currentWord = null;
        const short NUMBER_OF_IMAGES = 4;


        private string GetHtmlCode(string word) // Reference: https://stackoverflow.com/questions/27846337/select-and-download-random-image-from-google
        {
            string url = "https://www.google.com/search?q=" + word + "&tbm=isch";
            string data = "";

            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Accept = "text/html, application/xhtml+xml, */*";
            request.UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko";

            HttpWebResponse response = (HttpWebResponse)request.GetResponse();

            using (Stream dataStream = response.GetResponseStream())
            {
                if (dataStream == null)
                {
                    return "";
                }

                using (StreamReader sr = new StreamReader(dataStream))
                {
                    data = sr.ReadToEnd();
                }
            }
            return data;
        }
        private List<string> GetUrls(string html)
        {
            List<string> urls = (new Regex(@"(http(s?):)([/|.|\w|\s|-])*\.(?:jpg|png)").Matches(html)).Cast<Match>().Select(match => match.Value).ToList();
            urls.RemoveAll(url => url.StartsWith("https://www.gstatic.com") || url.StartsWith("https://ssl.gstatic.com"));
            return urls;
        }
        private List<string> GetImagesLinks(string word)
        {
            List<string> links = new List<string>();
            try
            {
                links = GetUrls(GetHtmlCode(word));
            }
            catch { /**/ }
            return links;
        }

        public List<string> GetNextImages(string word)
        {
            List<string> links = null;
            bool isANewWord = currentWord == null || currentWord != word;

            if (isANewWord) { currentWord = word; imagesLinks = GetImagesLinks(word); } // Update
            else if (imagesLinks.Count < NUMBER_OF_IMAGES)
            {
                imagesLinks = GetImagesLinks(word); // Update
            }

            // Pick the next 9 images
            links = imagesLinks.GetRange(0, NUMBER_OF_IMAGES);
            imagesLinks.RemoveRange(0, NUMBER_OF_IMAGES);

            return links;
        }

        public void reset()
        {
            currentWord = null;
            imagesLinks = null;
        }
    }
}
