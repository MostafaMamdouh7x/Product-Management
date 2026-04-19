package com.project.computers_store.repository;

import com.project.computers_store.model.Product;
import org.springframework.context.annotation.ReflectiveScan;
import org.springframework.data.jpa.repository.JpaRepository;
@ReflectiveScan
public  interface productRebository  extends JpaRepository<Product,Long> {
}
