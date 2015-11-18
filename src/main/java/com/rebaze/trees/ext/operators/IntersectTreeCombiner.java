/*
 * Copyright (c) 2015 rebaze GmbH
 * All rights reserved.
 *
 * This library and the accompanying materials are made available under the terms of the Apache License Version 2.0,
 * which accompanies this distribution and is available at http://www.apache.org/licenses/LICENSE-2.0.
 *
 */
package com.rebaze.trees.ext.operators;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.rebaze.trees.core.Tree;
import com.rebaze.trees.core.TreeBuilder;
import com.rebaze.trees.core.TreeCombiner;
import com.rebaze.trees.core.TreeIndex;
import com.rebaze.trees.core.util.TreeSession;

import static com.rebaze.trees.core.Tag.tag;

import java.util.HashMap;
import java.util.Map;

/**
 * This {@link TreeCombiner} creates a intersection tree.
 *
 * @author Toni Menzel (rebaze)
 * @since 0.3
 */
@Singleton
public class IntersectTreeCombiner implements TreeCombiner
{
    private final TreeSession session;

    @Inject
    public IntersectTreeCombiner( TreeSession session )
    {
        this.session = session;
    }

    @Override public Tree combine( Tree left, Tree right )
    {
        TreeBuilder builder = session.createTreeBuilder();
        walk(left, buildDeep(right),builder);
        return builder.seal();
    }

    private boolean walk(Tree left, Map<String, Tree> rightMap, TreeBuilder workinBranch) {
        //System.out.println("Testing " + left.fingerprint());
        if (rightMap.containsKey(left.fingerprint())) {
            // entire tree exists:
            workinBranch.branch(left).tag( tag ( "MATCH" ) );
            return true;
        }else {
            boolean res = false;
            TreeBuilder local = session.createTreeBuilder();
            for (Tree sub : left.branches()) {

                 res = walk(sub,rightMap,local.branch(sub.selector()));
            }
            if (res) {
                System.out.println("Adding " + local.seal());
                workinBranch.branch( local.seal() );
            }else {
                //System.out.println("Dropping " + local.seal());
            }
            return res;
            //
        }
    }

    private void walkB(Tree left, Map<String, Tree> rightMap, TreeBuilder workinBranch) {
        //System.out.println("Testing " + left.fingerprint());
        if (rightMap.containsKey(left.fingerprint())) {
            // entire tree exists:
            //System.out.println("Match " + left);
            workinBranch.branch(left).tag( tag ( "MATCH" ) );
        }else {
            for (Tree sub : left.branches()) {
                walkB( sub, rightMap, workinBranch.branch( sub.selector()));
            }
        }

    }

    static void identLeftHash( TreeBuilder collector, TreeIndex left, TreeIndex right, Map<String, Tree> index )
    {

        //if (index.containsKey(left.))
        if ( !left.fingerprint().equals( right.fingerprint() ) )
        {
            // dig deeper

        }
        else
        {
            // fast forward, copy whole branch.
            collector.branch( left );
        }
    }

    private static Map<String, Tree> buildDeep( Tree tree )
    {
        Map<String, Tree> index = new HashMap<String, Tree>();
        buildDeep( tree, index );
        return index;
    }

    private static void buildDeep( Tree tree, Map<String, Tree> index )
    {
        index.put( tree.fingerprint(), tree );
        for ( Tree sub : tree.branches() )
        {
            buildDeep( sub, index );
        }
    }

}
