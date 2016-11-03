package org.rebaze.integrity.tree.internal.operators;

import org.rebaze.integrity.tree.api.Tree;
import org.rebaze.integrity.tree.api.TreeCombiner;
import org.rebaze.integrity.tree.api.TreeSession;
import org.rebaze.integrity.tree.api.TreeBuilder;

/**
 * Rebuilds left with paths only where right is a leaf.
 *
 * Helpful for relocating subtrees within larger trees.
 *
 */
public class SubstructCombiner implements TreeCombiner
{
    private final TreeSession session;

    public SubstructCombiner( TreeSession session )
    {
        this.session = session;
    }

    @Override public Tree combine( Tree left, Tree right )
    {
        TreeBuilder tb = session.createTreeBuilder();
        walk(tb,left,right);
        return tb.seal();
    }

    private void walk( TreeBuilder tb, Tree base, Tree target )
    {
        boolean relevant = false;
        //depth first:
        for (Tree t : base.branches()) {
            if (t.equals( target )) {
                relevant = true;
            }else {

            }
            walk(tb,t,target);
        }
        if (relevant) {
            tb.branch( base );
        }
    }
}
