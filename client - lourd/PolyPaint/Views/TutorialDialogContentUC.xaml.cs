using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace FMUD.Views
{
    /// <summary>
    /// Interaction logic for TutorialDialogContentUC.xaml
    /// </summary>
    public partial class TutorialDialogContentUC : UserControl
    {
        public List<string> TutorialImages = new List<string>();
        public List<string> TutorialDescription = new List<string>();
        public int currentImageIndex = 0;
        public TutorialDialogContentUC()
        {
            InitializeComponent();
        }
        public void LaunchCompleteTutorial(object sender, RoutedEventArgs e)
        {
            TutorialImages.Add("../../Resources/Tutorials/overview/first.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/1.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/2.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/3.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/4.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/5.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/6.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/7.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/8.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/9.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/11.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/10.png");
            TutorialImages.Add("../../Resources/Tutorials/overview/last.png");


            TutorialDescription.Add("Welcome to Draw me something! This is game where you are rewarded for drawing well, and guessing well. Here is the main lobby");
            TutorialDescription.Add("This is your profile. You can see your parties history, your sessions, some statistics and your profile");
            TutorialDescription.Add("You can change your profile information clicking the circled button");
            TutorialDescription.Add("You only have to fill out what you need to change and press save to change your information");
            TutorialDescription.Add("Down here, you can filter your statistics depending on the party mode");
            TutorialDescription.Add("If you ever need to log out, the button is right here.");
            TutorialDescription.Add("This is the chat area. Everyone lands on the main channel and can chat in the main channel");
            TutorialDescription.Add("You can also create your own channels, or join other channels");
            TutorialDescription.Add("By clicking on a channel, you can start chatting in that channel and see its history");
            TutorialDescription.Add("In the middle section, you have a bottom and a top part. The bottom part shows you current party lobbies. Join a lobby by clicking the circled button");
            TutorialDescription.Add("You can see who is in a specific by pressing the circled button");
            TutorialDescription.Add("In the top part, you have a few things you can do, like getting help, contributing with game creation, create parties and filter parties");
            TutorialDescription.Add("Thanks for sticking through the tutorial! Read more subjects over there");
            showTutorial();
        }
        private void ShowNextCompleteTutorial(object sender, RoutedEventArgs e)
        {
            if (currentImageIndex < TutorialImages.Count)
            {
            }
        }
        private void LaunchDrawingGameTutorial(object sender, RoutedEventArgs e)
        {
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame1.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame2.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame3.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame4.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame5.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame6.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame7.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame8.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame9.png");
            TutorialImages.Add("../../Resources/Tutorials/DrawGame/TutorialDrawGame10.png");

            TutorialDescription.Add("To create a game, press the Contribute button in the main lobby");
            TutorialDescription.Add("This is the game creation menu. To draw your own game, press the circled button");
            TutorialDescription.Add("Here is the canvas. You can draw whatever you want here, using different tools such as the pencil shown here");
            TutorialDescription.Add("You can also spot erase your mistakes with the eraser");
            TutorialDescription.Add("Or you can erase an entire stroke using this tool");
            TutorialDescription.Add("You can also pick a round tip pen or a square tip pen, its width and its color");
            TutorialDescription.Add("You have to write down the secret word (what you drew), a few hints, the level of difficulty and the drawing mode");
            TutorialDescription.Add("You can preview the drawing being drawn using the preview button. You can also save your drawing as a new avatar with the button next to it.");
            TutorialDescription.Add("When you are satisfied with the drawing, save your drawing");
            TutorialDescription.Add("To finalize the creation, press the create button");


            showTutorial();
        }


        private void LaunchPickingGameTutorial(object sender, RoutedEventArgs e)
        {
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame1.png");
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame2.png");
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame3.png");
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame4.png");
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame5.png");
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame6.png");
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame8.png");
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame7.png");
            TutorialImages.Add("../../Resources/Tutorials/PickGame/TutorialPickGame9.png");

            TutorialDescription.Add("To create a game, press the Contribute button in the main lobby");
            TutorialDescription.Add("This is the game creation menu. To pick a pre-made game, press the circled button");
            TutorialDescription.Add("The page will show you a word and a drawing. Not satisfied? You can reload another drawing using the circled button.");
            TutorialDescription.Add("If you're satisfied, press the circled button to accept the drawing and word");
            TutorialDescription.Add("You can use the tools from the toolbar to draw whatever you want to add over the drawing");
            TutorialDescription.Add("Remember that at any point, you can save the drawing as your profile avatar");
            TutorialDescription.Add("Remember to save your drawing whenever you're satisfied to not lose any change");
            TutorialDescription.Add("You can preview how the drawing will be drawn according to the settings you have picked");
            TutorialDescription.Add("Fill out the information on bottom bar, then press create to create your game");


            showTutorial();
        }

        private void LaunchCreatePartyTutorial(object sender, RoutedEventArgs e)
        {
            TutorialImages.Add("../../Resources/Tutorials/PartyCreation/1.png");
            TutorialImages.Add("../../Resources/Tutorials/PartyCreation/2.png");

            TutorialImages.Add("../../Resources/Tutorials/PartyCreation/4.png");

            TutorialImages.Add("../../Resources/Tutorials/PartyCreation/6.png");


            TutorialDescription.Add("Want to create a party? Press the circled button to launch the creation menu");
            TutorialDescription.Add("Enter a name for your party. If you don't, a random one will be assigned");
            TutorialDescription.Add("Pick a game mode. For each game mode, there is a description for it right under");
            TutorialDescription.Add("When you're ready, you can create the party by pressing the circled button");

            showTutorial();
        }

        private void LaunchFFATutorial(object sender, RoutedEventArgs e)
        {
            TutorialImages.Add("../../Resources/Tutorials/ffa/1.png");
            TutorialImages.Add("../../Resources/Tutorials/ffa/2.png");
            TutorialImages.Add("../../Resources/Tutorials/ffa/3.png");
            TutorialImages.Add("../../Resources/Tutorials/ffa/4.png");
            TutorialImages.Add("../../Resources/Tutorials/ffa/11.png");
            TutorialImages.Add("../../Resources/Tutorials/ffa/5.png");
            TutorialImages.Add("../../Resources/Tutorials/ffa/6.png");
            TutorialImages.Add("../../Resources/Tutorials/ffa/7.png");



            TutorialDescription.Add("To play a free-for-all party, you must create it in the game creation menu. In the party lobby, you can add up to two virtual players.");
            TutorialDescription.Add("When there are at least 2 real players in the party lobby, you can press ready. When everyone is ready, the game starts.");
            TutorialDescription.Add("Notice that a new channel for the party has been created. You can discuss in this channel, give guesses and ask for hints");
            TutorialDescription.Add("This is the live party. The circled text tells you if you're the drawer, or if someone else is.");
            TutorialDescription.Add("When its your turn, the secret word will be showed to you. Draw the corresponding image as fast and as accurately as you can !");
            TutorialDescription.Add("Guess the secret word in the chat with the .g command. If you're right, you will be awarded points. If not, your number of trials decrease.");
            TutorialDescription.Add("Make sure to guess before the time runs out for each game. Remember, in FFA, you have to accumulate the most points to win against the others.");
            TutorialDescription.Add("You can always ask for a clue with the .c command. A bot will give you a clue.");

            showTutorial();
        }

        private void LaunchSoloTutorial(object sender, RoutedEventArgs e)
        {
            TutorialImages.Add("../../Resources/Tutorials/solo/1.png");
            TutorialImages.Add("../../Resources/Tutorials/solo/2.png");
            TutorialImages.Add("../../Resources/Tutorials/solo/3.png");
            TutorialImages.Add("../../Resources/Tutorials/solo/4.png");
            TutorialImages.Add("../../Resources/Tutorials/solo/5.png");
            TutorialImages.Add("../../Resources/Tutorials/solo/6.png");


            TutorialDescription.Add("To play a solo party, you must create it in the game creation menu. In the party lobby, press the circled button when ready.");
            TutorialDescription.Add("Notice a new channel for the party has been created. You can discuss in this channel, give guesses and ask for hints");
            TutorialDescription.Add("This is the drawing area. Your score is on the top left, and your number of guesses available before you lose are on the top right.");
            TutorialDescription.Add("Guess the secret word in the chat with the .g command. A bot will tell you wether you were right or wrong");
            TutorialDescription.Add("Whenever you guess right, or the drawing is done, or you've ran out of guesses, a new drawing immediately starts. Guess as much as you can before the timer runs out");
            TutorialDescription.Add("You can always ask for a clue with the .c command. A bot will give you a clue, but you might loose some points.");

            showTutorial();
        }

        private void LaunchCollabTutorial(object sender, RoutedEventArgs e)
        {
            TutorialImages.Add("../../Resources/Tutorials/coop/2.png");
            TutorialImages.Add("../../Resources/Tutorials/coop/3.png");
            TutorialImages.Add("../../Resources/Tutorials/coop/4.png");
            TutorialImages.Add("../../Resources/Tutorials/coop/5.png");
            TutorialImages.Add("../../Resources/Tutorials/coop/6.png");
            TutorialImages.Add("../../Resources/Tutorials/coop/7.png");


            TutorialDescription.Add("To play a cooperative party, you must create it in the game creation menu. In the party lobby, press ready when you are. The button is enabled when there are at least two players. The game starts when everyone is ready.");
            TutorialDescription.Add("Notice a new channel for the party has been created. You can discuss in this channel, give guesses and ask for hints");
            TutorialDescription.Add("This is the live party. In coop mode, only virtual players draw. Everyone in the game has to guess as many drawings as possible");
            TutorialDescription.Add("Guess the secret word in the chat with the .g command. If you're right, you will be awarded points. If not, your number of trials decrease");
            TutorialDescription.Add("Make sure to guess before the time runs out for each game. Remember, in coop, you play in collaboration with the others to score a cooperative high score");
            TutorialDescription.Add("You can always ask for a clue with the .c command. A bot will give you a clue, but you might loose some points.");

            showTutorial();
        }

        private void LaunchChatTutorial(object sender, RoutedEventArgs e)
        {
            TutorialImages.Add("../../Resources/Tutorials/chat/1.png");
            TutorialImages.Add("../../Resources/Tutorials/chat/2.png");
            TutorialImages.Add("../../Resources/Tutorials/chat/3.png");
            TutorialImages.Add("../../Resources/Tutorials/chat/4.png");
            TutorialImages.Add("../../Resources/Tutorials/chat/5.png");



            TutorialDescription.Add("The first command, .h, lets you view the current channel's history.");
            TutorialDescription.Add("Just type .h, then enter to load older messages from the channel.");
            TutorialDescription.Add("Seems like Parfait has been talking to himself.");
            TutorialDescription.Add("While in a party, guess the secret word in the chat with the .g command. A chatbot will inform you of wether you were right, or wrong.");
            TutorialDescription.Add("While in a party, you can always ask for a clue when they are available with the .c command. A chatbot will give you a clue.");

            showTutorial();
        }

        private void ShowNextTutorialSlide(object sender, RoutedEventArgs e)
        {
            currentImageIndex++;
            if (currentImageIndex < TutorialImages.Count)
            {
                ImageBrush imageB = new ImageBrush();
                BitmapImage btpImg = new BitmapImage(new Uri(TutorialImages[currentImageIndex], UriKind.Relative));
                imageB.ImageSource = btpImg;
                TutorialImageContainer.Background = imageB;
                TutorialImageDescription.Text = TutorialDescription[currentImageIndex];
            }
            else
            {
                resetTutorial(ShowNextTutorialSlide);
            }

        }

        private void resetTutorial(RoutedEventHandler h)
        {
            TutorialTree.Visibility = Visibility.Visible;
            TutorialImageContainer.Visibility = Visibility.Collapsed;
            TutorialDescriptionContainer.Visibility = Visibility.Collapsed;
            currentImageIndex = 0;
            TutorialImages.Clear();
            TutorialDescription.Clear();
            TutoSeparator.Visibility = Visibility.Collapsed;
            if (h != null)
            {
                TutorialNextButton.Click -= h;
            }
            // la partie qui suit sert a deselectionner
            TreeViewItem item = TutorialTree.SelectedItem as TreeViewItem;
            if (item != null)
            {
                TutorialTree.Focus();
                item.IsSelected = false;
            }
        }

        public void resetWhenTutorialClosed(object sender, RoutedEventArgs e)
        {
            TutorialTree.Visibility = Visibility.Visible;
            TutorialImageContainer.Visibility = Visibility.Collapsed;
            TutorialDescriptionContainer.Visibility = Visibility.Collapsed;
            currentImageIndex = 0;
            TutorialImages.Clear();
            TutorialDescription.Clear();
            TutoSeparator.Visibility = Visibility.Collapsed;

            TreeViewItem item = TutorialTree.SelectedItem as TreeViewItem;
            if (item != null)
            {
                TutorialTree.Focus();
                item.IsSelected = false;
            }
        }

        private void showTutorial()
        {
            TutorialTree.Visibility = Visibility.Collapsed;
            TutorialImageContainer.Visibility = Visibility.Visible;
            TutorialDescriptionContainer.Visibility = Visibility.Visible;
            TutoSeparator.Visibility = Visibility.Visible;

            ImageBrush imageB = new ImageBrush();
            BitmapImage btpImg = new BitmapImage(new Uri(TutorialImages[currentImageIndex], UriKind.Relative));
            imageB.ImageSource = btpImg;
            TutorialImageContainer.Background = imageB;
            TutorialImageDescription.Text = TutorialDescription[currentImageIndex];
            TutorialNextButton.Click += ShowNextTutorialSlide;
        }
    }
}
