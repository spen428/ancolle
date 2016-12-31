package ancolle.items;

/**
 * Product types
 *
 * @author samuel
 */
public enum ProductType {
    FRANCHISE("Franchise"), GAME("Game"), VIDEO("Video"), ANIMATION("Animation"),
    OTHER("Other"), RADIO_AND_DRAMA("Radio & Drama"), UNKNOWN("");

    private static final ProductType[] values = ProductType.values();

    public final String typeString;

    ProductType(String typeString) {
        this.typeString = typeString;
    }

    /**
     * Find the {@link ProductType} whose type string equals the given string.
     *
     * @param str the string
     * @return the {@link ProductType} or {@link ProductType#UNKNOWN} if none
     * was found
     */
    public static ProductType getProductTypeFromString(String str) {
        for (ProductType pt : values) {
            if (pt.typeString.equals(str)) {
                return pt;
            }
        }
        return UNKNOWN;
    }

}
