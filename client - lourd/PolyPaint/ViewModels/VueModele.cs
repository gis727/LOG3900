using FMUD.Models;
using FMUD.Utilities;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Ink;
using System.Windows.Media;

namespace FMUD.VueModeles
{
    class VueModele : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;
        public Editeur editeur = new Editeur();
        private bool canRedraw = false;

        // Attributes defining the appearence of a line
        public DrawingAttributes AttributsDessin { get; set; } = new DrawingAttributes();

        public bool CanRedraw
        {
            get { return canRedraw; }
            set { canRedraw = value; ProprieteModifiee(); }
        }

        public string OutilSelectionne
        {
            get { return editeur.OutilSelectionne; }
            set { ProprieteModifiee(); }
        }

        public string CouleurSelectionnee
        {
            get { return editeur.CouleurSelectionnee; }
            set { editeur.CouleurSelectionnee = value; ProprieteModifiee(); }
        }

        public string PointeSelectionnee
        {
            get { return editeur.PointeSelectionnee; }
            set { ProprieteModifiee(); }
        }

        public int TailleTrait
        {
            get { return editeur.TailleTrait; }
            set { editeur.TailleTrait = value; }
        }

        public StrokeCollection Traits { get; set; }

        public RelayCommand<string> ChoisirPointe { get; set; }
        public RelayCommand<string> ChoisirOutil { get; set; }
        public RelayCommand<object> Reinitialiser { get; set; }


        public VueModele()
        {
            editeur.PropertyChanged += new PropertyChangedEventHandler(EditeurProprieteModifiee);

            // Drawing initial values
            AttributsDessin = new DrawingAttributes
            {
                Color = (Color)ColorConverter.ConvertFromString(editeur.CouleurSelectionnee)
            };
            AjusterPointe();

            Traits = editeur.traits;

            ChoisirPointe = new RelayCommand<string>(editeur.ChoisirPointe);
            ChoisirOutil = new RelayCommand<string>(editeur.ChoisirOutil);
            Reinitialiser = new RelayCommand<object>(editeur.Reinitialiser);
        }

        protected virtual void ProprieteModifiee([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }

        /// <summary>
        /// Handles events coming from the model
        /// </summary>
        private void EditeurProprieteModifiee(object sender, PropertyChangedEventArgs e)
        {
            if (e.PropertyName == "CouleurSelectionnee")
            {
                AttributsDessin.Color = (Color)ColorConverter.ConvertFromString(editeur.CouleurSelectionnee);
            }
            else if (e.PropertyName == "OutilSelectionne")
            {
                OutilSelectionne = editeur.OutilSelectionne;
            }
            else if (e.PropertyName == "PointeSelectionnee")
            {
                PointeSelectionnee = editeur.PointeSelectionnee;
                AjusterPointe();
            }
            else // e.PropertyName == "TailleTrait"
            {
                AjusterPointe();
            }
        }

        /// <summary>
        /// The form and the size of the crayon's tip are defined here
        /// </summary>
        private void AjusterPointe()
        {
            AttributsDessin.StylusTip = (editeur.PointeSelectionnee == "ronde") ? StylusTip.Ellipse : StylusTip.Rectangle;
            AttributsDessin.Width = (editeur.PointeSelectionnee == "verticale") ? 1 : editeur.TailleTrait;
            AttributsDessin.Height = (editeur.PointeSelectionnee == "horizontale") ? 1 : editeur.TailleTrait;
        }

        // Helpers
        public bool PtByPtEraserSelected()
        {
            return editeur.OutilSelectionne == "efface_segment";
        }
        public bool StkByStkEraserSelected()
        {
            return editeur.OutilSelectionne == "efface_trait";
        }
        public bool EraserSelected()
        {
            return PtByPtEraserSelected() || StkByStkEraserSelected();
        }
    }
}
