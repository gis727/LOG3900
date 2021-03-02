using Microsoft.Win32;
using PolyPaint.Models.Coms;
using PolyPaint.Services;
using PolyPaint.Utilities;
using PolyPaint.ViewModels;
using System;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace PolyPaint.Views
{
    /// <summary>
    /// Interaction logic for ProfileUC.xaml
    /// </summary>
    public partial class ProfileUC : UserControl
    {
        bool handleFilter = false;
        public ProfileUC()
        {
            InitializeComponent();
            DataContext = new ProfileVM();
            ServerService.OnModifyAccount((response) =>
            {
                Application.Current.Dispatcher.Invoke(() =>
                {
                    if ((STATE)response.state == STATE.UsedNickname)
                    {
                        ErrorText.Text = "Username is already in use";
                        ErrorText.Foreground = Brushes.Red;
                    }
                    else
                    {
                        ErrorText.Text = "Successfully updated";
                        ErrorText.Foreground = Brushes.LimeGreen;
                    }
                });
            });
        }
        private void Avatar_Button_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog
            {
                Title = "Choose an avatar",
                Filter = "Image files only (*.png;*.jpeg;*.jpg;*.bmp)|*.png;*.jpeg;*.jpg;*.bmp|All files (*.*)|*.*"
            };
            if (dialog.ShowDialog() == true)
            {
                AvatarImage.Source = new BitmapImage(new Uri(dialog.FileName));
                AvatarImage.Visibility = Visibility.Visible;
                AvatarIcon.Visibility = Visibility.Collapsed;
                SubmitButton.IsEnabled = CredentialsAreValid();
            }
        }

        private void Submit_Button_Click(object sender, RoutedEventArgs e)
        {

            ServerService.ModifyAccount(FirstName.Text, LastName.Text, Password.Password, NickName.Text,
                (new ImageConverter()).ToBase64String((BitmapImage)AvatarImage.Source, AvatarImage.Source.ToString()));
        }
        private bool CredentialsAreValid()
        {
            return Password.Password.Trim().Length > 0 && FirstName.Text.Trim().Length > 0 &&
                                     LastName.Text.Trim().Length > 0 && NickName.Text.Trim().Length > 0;
        }

        private void Credentials_Changed(object sender, RoutedEventArgs e)
        {
            bool enable = CredentialsAreValid() && !AvatarImage.Source.ToString().Equals("pack://application:,,,/PolyPaint;component/Resources/avatar.jpg");
            SubmitButton.IsEnabled = enable;
            ErrorText.Foreground = Brushes.Gray;
            ErrorText.Text = enable ? "" : "Please complete all the fields";
        }

        private void Account_Edit_Button_Click(object sender, RoutedEventArgs e)
        {
            Password.Password = "";
        }

        private void Filter_Selection_Changed(object sender, RoutedEventArgs e)
        {
            ComboBox cmb = sender as ComboBox;
            handleFilter = !cmb.IsDropDownOpen;
            HandleFilter();
        }
        private void OnDropDownClosed(object sender, EventArgs e)
        {
            if (handleFilter)
            {
                HandleFilter();
            }

            handleFilter = true;
        }

        private void HandleFilter()
        {
            if (cmbFilter.SelectedItem.ToString() != null)
            {
                string filterText = cmbFilter.SelectedItem.ToString().Split(new string[] { ": " }, StringSplitOptions.None).Last();
                switch (filterText)
                {
                    case "Solo Sprint":
                        ((ProfileVM)DataContext).Filter(GameMode.solo);
                        break;
                    case "Collaborative Sprint":
                        ((ProfileVM)DataContext).Filter(GameMode.coop);
                        break;
                    case "Free-for-all":
                        ((ProfileVM)DataContext).Filter(GameMode.ffa);
                        break;
                    case "All":
                        ((ProfileVM)DataContext).Filter(GameMode.none);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
