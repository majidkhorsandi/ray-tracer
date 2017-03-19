package domain;

/**
 * Implementation of Vector type
 * A vector is composed by 3 double numbers   
 * @author Behzad Oskooi
 */

public class Vector {
    private final double xpos;
    private final double ypos;
    private final double zpos;

    /**
     * default constructor for vector class sets Vector's coordinates to zero values
     */
    public Vector(){
        xpos = 0;
        ypos = 0;
        zpos = 0;
    }
        
    /**
     * constructor to create a Vector with its coordinates                  
     * @param xpos, X coordinate of vector
     * @param ypos, Y coordinate of vector
     * @param zpos, Z coordinate of vector
     */
    public Vector(final double xcord,
                  final double ycord,
                  final double zcord) {
        this.xpos = xcord;
        this.ypos = ycord;
        this.zpos = zcord;
    }

        
    /**
     * constructor to create a Vector based on another vectors coordinate
     * @param xpos
     */
    public Vector(final Vector vect) {
        this.xpos = vect.xpos;
        this.ypos = vect.ypos;
        this.zpos = vect.zpos;

    }
        
    /**
     * computes dot product value of vector v and current vector 
     * @param vect a vector we want to calculate its dot product 
     * with current vector.
     * @return  a double value , dot product of 2 vectors
     */
    public double dotProduct(final Vector vect){
        return (this.xpos * vect.xpos + 
                this.ypos * vect.ypos + 
                this.zpos * vect.zpos);
    }    

    /**
     * computes cross product of vector v and current vector
     * @param vect which is a vector we want to calculate its dot product 
     * with current vector.
     * @return a vector which cross product of 2 vectors
     */
    public Vector crossProduct(final Vector vect){
        return new Vector(this.ypos * vect.zpos - this.zpos * vect.ypos,
                            this.zpos * vect.xpos - this.xpos * vect.zpos,
                            this.xpos * vect.ypos - this.ypos * vect.xpos);
    }
        
    /**
     * does the addition between two vectors                   
     * @param vect a vector we want to add to current vector
     * @return result of addition as vector
     */
    public Vector vectorAddition(final Vector vect){
        return new Vector(this.xpos + vect.xpos,
                            this.ypos + vect.ypos,
                            this.zpos + vect.zpos);
    }
        
    /**
     * does the reduction between two vectors
     * @param vect a vector we want to reduce from current vector
     * @return result of reduction as vector.
     */
    public Vector vectorReduction(final Vector vect){
        return new Vector(this.xpos - vect.xpos,
                            this.ypos - vect.ypos,
                            this.zpos - vect.zpos);
    }

    /**
     * this overload gets a single float parameter s, multiplies current
     * vector by s and returns resulting vector as output.
     * @param scale a float number which will be multiplied by current vector.
     * @return a vector which is result of multiplying current vector by s.
     */
    public Vector vectorMultiply(final double scale){
        return new Vector(this.xpos * scale,
                            this.ypos * scale,
                            this.zpos * scale);
    }
        
    /**
     * computes a vector length
     * @return a float value indicates length of vector vect.
     */
    public double vectorLength(final Vector vect){
        return Math.sqrt((vect.xpos * vect.xpos) +
                         (vect.ypos * vect.ypos) + 
                         (vect.zpos * vect.zpos));
    }
        
    /**
     * computes this vector's length
     * @return a float value indicates length of current vector.
     */
    public double vectorLength(){
        return Math.sqrt((this.xpos * this.xpos) +
                         (this.ypos * this.ypos) + 
                         (this.zpos * this.zpos));
    }

    /**
     * computes current vector's length
     * @return a float value
     */
    public double magnitude(){
        return Math.sqrt((xpos * xpos) +
                         (ypos * ypos) +
                         (zpos * zpos));
    }
        
    /**
     * normalizes current vector
     * @return a Vector
     */
    public Vector normalize(){
        final double tmp = this.magnitude();
        return new Vector(xpos/tmp, ypos/tmp, zpos/tmp);
    }
        
    /**
     * tests whether current vector is normalized or not             
     * @return true if vector is normalized and false if it is not.
     */
    public boolean normalizeCheck(){
        final double test = 
            (this.xpos * this.xpos) +
            (this.ypos * this.ypos) +
            (this.zpos * this.zpos);
        if(Double.compare(test, 1.0) == 0){
            return true;
        }
        else {
            return false;
        }
    }
        
    /**
     * tests whether two vectors equal or not 
     * @param vect1 Vector number one
     * @param vect2 Vector number two
     * @return true if both vectors are equal, if not return false
     */
    @Override 
    public boolean equals(final Object vect) {
        if(this == vect){return true;}
        if(!(vect instanceof Vector)) {return false;}
        final Vector newv = (Vector)vect;
                
        if((newv.xpos - this.xpos <= 0.00001 ) && 
           (newv.ypos - this.ypos <= 0.00001 ) && 
           (newv.zpos - this.zpos <= 0.00001)){
            return true;
        }
        return false;           
    }
        
    /**
     * this is the hashcode() method to support class equals() method
     */
    @Override public int hashCode(){
        final long value = Double.doubleToLongBits(xpos + ypos + zpos);
        return (int) (value ^ (value >> 32));
    }
        
    /**
     * returns the xpos,ypos,zpos as string
     */
    @Override public String toString(){
        return String.format("{0},{1},{2}", xpos, ypos, zpos);
    }
}