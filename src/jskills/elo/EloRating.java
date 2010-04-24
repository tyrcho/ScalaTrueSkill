
namespace Moserware.Skills.Elo
{
    /**
     * An Elo rating represented by a single number (mean).
     */
    public class EloRating : Rating
    {
        public EloRating(double rating)
            : base(rating, 0)
        {
        }
    }
}
