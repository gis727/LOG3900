using System;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Resources;
using Point = System.Windows.Point;

namespace PolyPaint.Utilities
{
    class ImageConverter
    {
        const int CanvasRefHeight = 450;
        const int CanvasRefWidth = 500;

        public string ToBase64String(BitmapImage image, string source = null, BitmapSource bmpSource = null)
        {
            string extension = GetExtension(source);
            MemoryStream memStream = new MemoryStream();
            string header = "data:image/";
            if (extension.Equals("png") || bmpSource != null)
            {
                header += "png;base64,";
                PngBitmapEncoder encoder = new PngBitmapEncoder();
                encoder.Frames.Add(BitmapFrame.Create(bmpSource != null ? bmpSource : image));
                encoder.Save(memStream);
            }
            else if (extension.Equals("jpg") || extension.Equals("jpeg"))
            {
                header += "jpeg;base64,";
                JpegBitmapEncoder encoder = new JpegBitmapEncoder();
                encoder.Frames.Add(BitmapFrame.Create(image));
                encoder.Save(memStream);
            }
            else if (extension.Equals("bmp"))
            {
                header += "bmp;base64,";
                BmpBitmapEncoder encoder = new BmpBitmapEncoder();
                encoder.Frames.Add(BitmapFrame.Create(image));
                encoder.Save(memStream);
            }
            return header + Convert.ToBase64String(memStream.ToArray());
        }

        public string GetBase64FromCanvas(InkCanvas canvas)
        {
            RenderTargetBitmap target = new RenderTargetBitmap((int)(canvas.RenderSize.Width), (int)(canvas.RenderSize.Height), 96, 96, PixelFormats.Pbgra32);
            VisualBrush brush = new VisualBrush(canvas);

            DrawingVisual visual = new DrawingVisual();
            DrawingContext drawingContext = visual.RenderOpen();


            drawingContext.DrawRectangle(brush, null, new Rect(new Point(0, 0),
                new Point(canvas.RenderSize.Width, canvas.RenderSize.Height)));

            drawingContext.Close();

            target.Render(visual);
            return ToBase64String(null, null, target);
        }

        // POTRACE stuff
        public string GetSvgStringFromPotrace(Bitmap image, string source, PotraceOptions options)
        {
            string potraceDir = Path.GetTempPath() + "FMUD";
            string potracePath = potraceDir + "/potrace.exe";
            string[] splittedString = source.Split('\\');
            string fileName = splittedString[splittedString.Length - 1];
            string filePath = potraceDir + "/" + fileName;
            string bmpFilePath = filePath.Replace(GetExtension(source), "bmp");
            string svgFilePath = filePath.Replace(GetExtension(source), "svg");
            string svgString = "";
            try
            {
                // Clean first
                if (Directory.Exists(potraceDir))
                {
                    SilentlyDelete(potraceDir);
                }
                // Write potrace to the temp folder
                Directory.CreateDirectory(potraceDir);
                StreamResourceInfo stream = Application.GetResourceStream(new Uri("Resources/potrace.exe", UriKind.Relative));
                using (FileStream exeFile = new FileStream(potracePath, FileMode.Create))
                {
                    stream.Stream.CopyTo(exeFile);
                    exeFile.Dispose();
                    stream.Stream.Dispose();
                }
                // Write the image to the temp folder
                using (FileStream imgFile = new FileStream(bmpFilePath, FileMode.CreateNew))
                {
                    image.Save(imgFile, ImageFormat.Bmp);
                    image.Dispose();
                }
                // Run potrace
                using (Process exeProcess = Process.Start(potracePath, options.GetOptionsAsString() + bmpFilePath))
                {
                    exeProcess.WaitForExit();
                    if (exeProcess.ExitCode != 0)
                    {
                        throw new Exception("Potrace couldn't process the image.");
                    }
                }
                // Get the string
                svgString = string.Join(" ", File.ReadAllText(svgFilePath).Split(new char[] { '\n', '\r' }, StringSplitOptions.RemoveEmptyEntries));
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("Unable to use potrace : " + e.Message);
                Console.Error.WriteLine(e.StackTrace);
            }
            finally
            {
                SilentlyDelete(potraceDir);
            }
            return svgString;
        }
        private string GetExtension(string source = null)
        {
            if (source == null)
            {
                return "";
            }

            string[] splitted = source.Split('.');
            return splitted[splitted.Length - 1];
        }
        private void SilentlyDelete(string path)
        {
            try { Directory.Delete(path, true); } catch (Exception) { }
        }

        public Bitmap BitmapImageToBitmap(BitmapImage bitmapImage, string source, bool resize = true)
        {
            using (MemoryStream outStream = new MemoryStream())
            {
                BitmapEncoder enc = new BmpBitmapEncoder();
                enc.Frames.Add(BitmapFrame.Create(bitmapImage, null, null, null));
                enc.Save(outStream);
                Bitmap bitmap = new System.Drawing.Bitmap(outStream);
                return (resize) ? new System.Drawing.Bitmap(bitmap, new System.Drawing.Size(CanvasRefWidth, CanvasRefHeight)) : bitmap;
            }
        }
    }

    public class PotraceOptions
    {
        readonly bool OptimzeCurve = true;
        readonly bool WhiteShapeOpaque = false;
        readonly bool InvertColors = false;
        readonly bool Rotate = false;
        readonly int Rotation = 0;

        public PotraceOptions(bool optimzeCurve, bool whiteShapeOpaque, bool invertColors, bool rotate, int rotation = 0)
        {
            OptimzeCurve = optimzeCurve;
            WhiteShapeOpaque = whiteShapeOpaque;
            InvertColors = invertColors;
            Rotate = rotate;
            Rotation = Math.Abs(rotation);
        }

        public string GetOptionsAsString()
        {
            string optionsString = " --svg";
            if (OptimzeCurve)
            {
                optionsString += " --longcurve";
            }

            if (WhiteShapeOpaque)
            {
                optionsString += " --opaque";
            }

            if (InvertColors)
            {
                optionsString += " --invert";
            }

            if (Rotate)
            {
                optionsString += " --rotate " + Rotation;
            }

            return optionsString + " ";
        }
    }
}
