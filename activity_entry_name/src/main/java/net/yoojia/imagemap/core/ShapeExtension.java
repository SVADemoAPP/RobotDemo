package net.yoojia.imagemap.core;

/**
 * ShapeExtension是出于ImageMap继承于FrameLayout而又需要嵌入到HighlightImageView内部过程而设计的。
 * 主要是将HighlhgitImageView内部过程的操作扩展到ImageMap中处理。
 */
public interface ShapeExtension
{

    public interface OnShapeActionListener
    {
        /**
         * 当一个SpecialShape被点击
         * 
         * @param shape
         * @param xOnImage
         * @param yOnImage
         */
        void onSpecialShapeClick(SpecialShape shape, float xOnImage, float yOnImage);
        
        /**
         * 当一个PushMessageShape被点击
         * 
         * @param shape
         * @param xOnImage
         * @param yOnImage
         */
        void onPushMessageShapeClick(PushMessageShape shape, float xOnImage, float yOnImage);
        
        /**
         * 当一个CollectPointShape被点击
         * 
         * @param shape
         * @param xOnImage
         * @param yOnImage
         */
        void onCollectShapeClick(CollectPointShape shape, float xOnImage, float yOnImage);
        
        /**
         * 当一个MoniPointShape被点击
         * 
         * @param shape
         * @param xOnImage
         * @param yOnImage
         */
        void onMoniShapeClick(MoniPointShape shape, float xOnImage, float yOnImage);
        
        /**
         * 点击shape外的回调
         * @param xOnImage
         * @param yOnImage
         */
        void outShapeClick(float xOnImage, float yOnImage);
    }

    /**
     * 添加形状
     * 
     * @param shape
     *            形状描述
     */
    void addShape(Shape shape, boolean isMove);

    /**
     * 删除指定Tag的形状
     * 
     * @param tag
     *            指定Tag
     */
    void removeShape(Object tag);

    /**
     * 清除所有形状
     */
    void clearShapes();
}