using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Ink;

namespace FMUD.Models
{
    class Editeur : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;
        public StrokeCollection traits = new StrokeCollection();
        private readonly StrokeCollection traitsRetires = new StrokeCollection();

        // Currently selected tool
        private string outilSelectionne = "crayon";
        public string OutilSelectionne
        {
            get { return outilSelectionne; }
            set { outilSelectionne = value; ProprieteModifiee(); }
        }

        // Crayon's tip form
        private string pointeSelectionnee = "ronde";
        public string PointeSelectionnee
        {
            get { return pointeSelectionnee; }
            set
            {
                OutilSelectionne = "crayon";
                pointeSelectionnee = value;
                ProprieteModifiee();
            }
        }

        // Drawing color
        private string couleurSelectionnee = "Black";
        public string CouleurSelectionnee
        {
            get { return couleurSelectionnee; }
            // Crayon automatically selected after a new color has been selected
            set
            {
                couleurSelectionnee = value;
                ProprieteModifiee();
            }
        }

        // Drawing lines size
        private int tailleTrait = 11;
        public int TailleTrait
        {
            get { return tailleTrait; }
            set
            {
                tailleTrait = value;
                ProprieteModifiee();
            }
        }

        protected void ProprieteModifiee([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

        public void ChoisirPointe(string pointe) => PointeSelectionnee = pointe;

        public void ChoisirOutil(string outil) => OutilSelectionne = outil;

        public void Reinitialiser(object o) => traits.Clear();
    }
}