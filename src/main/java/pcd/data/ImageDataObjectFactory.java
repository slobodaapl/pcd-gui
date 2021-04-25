package pcd.data;

/**
 *
 * @author ixenr
 * 
 * A factory class for {@link ImageDataObject}.
 * <p>
 * It is currently unnecessary but is present for potential further expansion
 * and improvements.
 */
public class ImageDataObjectFactory {

    /**
     * An instance of {@link ImageDataStorage} to add new images
     */
    private final ImageDataStorage store;

    /**
     * Instantiates a new {@link ImageDataObjectFactory}
     * @param store an instantiated {@link ImageDataStorage}
     */
    public ImageDataObjectFactory(ImageDataStorage store) {
        this.store = store;
    }

    /**
     * Creates new {@link ImageDataObject} from path to image
     * @param path a {@link String} path to image
     * @return a new instance of {@link ImageDataObject}
     */
    public ImageDataObject makeImage(String path) {
        return new ImageDataObject(path);
    }

    /**
     * @deprecated
     * 
     * Appends new {@link ImageDataObject} to {@link ImageDataStorage}.
     * <p>
     * This is not how a factory class is supposed to function and it will be
     * removed in the future. Use {@link ImageDataObjectFactory#makeImage(java.lang.String)} instead.
     * 
     * @param path a {@link String} path to the image to be associated with {@link ImageDataObject}
     */
    public void addImage(String path) {
        store.addImage(makeImage(path));
    }

}
