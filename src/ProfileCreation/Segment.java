/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProfileCreation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 16536096
 */
public abstract class Segment {

    protected Node start;
    protected Node end;

    public Node getStart() {
        return start;
    }

    public Node getEnd() {

        return end;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public void setEnd(Node end) {
        this.end = end;

    }

}
