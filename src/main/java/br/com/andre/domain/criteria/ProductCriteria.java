package br.com.andre.domain.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link br.com.andre.domain.Product} entity. This class is used
 * in {@link br.com.andre.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter slug;

    private BigDecimalFilter price;

    private BigDecimalFilter promotionalPrice;

    private StringFilter sku;

    private StringFilter imageUrl;

    private IntegerFilter stock;

    private BooleanFilter active;

    private InstantFilter createdDate;

    private InstantFilter updatedDate;

    private LongFilter categoryId;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.price = other.optionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.promotionalPrice = other.optionalPromotionalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.sku = other.optionalSku().map(StringFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.stock = other.optionalStock().map(IntegerFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.updatedDate = other.optionalUpdatedDate().map(InstantFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSlug() {
        return slug;
    }

    public Optional<StringFilter> optionalSlug() {
        return Optional.ofNullable(slug);
    }

    public StringFilter slug() {
        if (slug == null) {
            setSlug(new StringFilter());
        }
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public Optional<BigDecimalFilter> optionalPrice() {
        return Optional.ofNullable(price);
    }

    public BigDecimalFilter price() {
        if (price == null) {
            setPrice(new BigDecimalFilter());
        }
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public BigDecimalFilter getPromotionalPrice() {
        return promotionalPrice;
    }

    public Optional<BigDecimalFilter> optionalPromotionalPrice() {
        return Optional.ofNullable(promotionalPrice);
    }

    public BigDecimalFilter promotionalPrice() {
        if (promotionalPrice == null) {
            setPromotionalPrice(new BigDecimalFilter());
        }
        return promotionalPrice;
    }

    public void setPromotionalPrice(BigDecimalFilter promotionalPrice) {
        this.promotionalPrice = promotionalPrice;
    }

    public StringFilter getSku() {
        return sku;
    }

    public Optional<StringFilter> optionalSku() {
        return Optional.ofNullable(sku);
    }

    public StringFilter sku() {
        if (sku == null) {
            setSku(new StringFilter());
        }
        return sku;
    }

    public void setSku(StringFilter sku) {
        this.sku = sku;
    }

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public Optional<StringFilter> optionalImageUrl() {
        return Optional.ofNullable(imageUrl);
    }

    public StringFilter imageUrl() {
        if (imageUrl == null) {
            setImageUrl(new StringFilter());
        }
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public IntegerFilter getStock() {
        return stock;
    }

    public Optional<IntegerFilter> optionalStock() {
        return Optional.ofNullable(stock);
    }

    public IntegerFilter stock() {
        if (stock == null) {
            setStock(new IntegerFilter());
        }
        return stock;
    }

    public void setStock(IntegerFilter stock) {
        this.stock = stock;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public InstantFilter getUpdatedDate() {
        return updatedDate;
    }

    public Optional<InstantFilter> optionalUpdatedDate() {
        return Optional.ofNullable(updatedDate);
    }

    public InstantFilter updatedDate() {
        if (updatedDate == null) {
            setUpdatedDate(new InstantFilter());
        }
        return updatedDate;
    }

    public void setUpdatedDate(InstantFilter updatedDate) {
        this.updatedDate = updatedDate;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(price, that.price) &&
            Objects.equals(promotionalPrice, that.promotionalPrice) &&
            Objects.equals(sku, that.sku) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(stock, that.stock) &&
            Objects.equals(active, that.active) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(updatedDate, that.updatedDate) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            slug,
            price,
            promotionalPrice,
            sku,
            imageUrl,
            stock,
            active,
            createdDate,
            updatedDate,
            categoryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalPrice().map(f -> "price=" + f + ", ").orElse("") +
            optionalPromotionalPrice().map(f -> "promotionalPrice=" + f + ", ").orElse("") +
            optionalSku().map(f -> "sku=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalStock().map(f -> "stock=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalUpdatedDate().map(f -> "updatedDate=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
