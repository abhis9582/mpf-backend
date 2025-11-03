# Database Schema Update for User Property Submission

## Analysis Summary

✅ **We can use the existing Project table!**
The Project entity already has most required fields and relationships.

### What Already Exists:
- ✅ projectName, projectLocality, projectConfiguration, projectPrice
- ✅ city, builder, projectTypes relationships
- ✅ amenities, galleries, floor plans relationships
- ✅ LOB fields for descriptions

### What We Need to Add:
1. Additional property details (bedrooms, bathrooms, floor, etc.)
2. Contact information
3. User submission tracking
4. Approval workflow fields

---

## SQL Migration Script

```sql
-- ==========================================
-- STEP 1: Add user submission tracking fields
-- ==========================================

ALTER TABLE projects 
ADD COLUMN submitted_by_id INT AFTER project_status_id,
ADD COLUMN approval_status VARCHAR(20) DEFAULT 'APPROVED',
ADD COLUMN rejection_reason TEXT,
ADD COLUMN submitted_at TIMESTAMP,
ADD COLUMN approved_at TIMESTAMP,
ADD COLUMN approved_by_id INT,
ADD COLUMN is_user_submitted BOOLEAN DEFAULT FALSE;

-- Add foreign keys for user tracking
ALTER TABLE projects
ADD CONSTRAINT fk_projects_submitted_by FOREIGN KEY (submitted_by_id) REFERENCES users(id) ON DELETE SET NULL,
ADD CONSTRAINT fk_projects_approved_by FOREIGN KEY (approved_by_id) REFERENCES users(id) ON DELETE SET NULL;

-- ==========================================
-- STEP 2: Add additional property details
-- ==========================================

ALTER TABLE projects
ADD COLUMN bedrooms INT,
ADD COLUMN bathrooms INT,
ADD COLUMN balconies INT,
ADD COLUMN floor_number INT,
ADD COLUMN total_floors INT,
ADD COLUMN facing VARCHAR(50),
ADD COLUMN age_of_construction INT,
ADD COLUMN carpet_area_sqft DOUBLE,
ADD COLUMN built_up_area_sqft DOUBLE,
ADD COLUMN super_built_up_area_sqft DOUBLE,
ADD COLUMN plot_area_sqft DOUBLE,
ADD COLUMN price_per_sqft DOUBLE,
ADD COLUMN maintenance_charges DOUBLE,
ADD COLUMN booking_amount DOUBLE,
ADD COLUMN furnished_status VARCHAR(50),
ADD COLUMN parking_details VARCHAR(100),
ADD COLUMN transaction_type VARCHAR(20) DEFAULT 'Sale' COMMENT 'Sale or Rent',
ADD COLUMN listing_type VARCHAR(20) COMMENT 'Residential or Commercial',
ADD COLUMN property_subtype VARCHAR(50),
ADD COLUMN possession_status VARCHAR(50),
ADD COLUMN occupancy_status VARCHAR(50),
ADD COLUMN notice_period INT COMMENT 'In days';

-- ==========================================
-- STEP 3: Add contact information
-- ==========================================

ALTER TABLE projects
ADD COLUMN contact_name VARCHAR(255),
ADD COLUMN contact_phone VARCHAR(20),
ADD COLUMN contact_email VARCHAR(100),
ADD COLUMN preferred_time VARCHAR(50),
ADD COLUMN additional_notes TEXT;

-- ==========================================
-- STEP 4: Update existing projects
-- ==========================================

UPDATE projects SET 
    approval_status = 'APPROVED',
    is_user_submitted = FALSE,
    transaction_type = 'Sale',
    listing_type = 'Residential';

-- ==========================================
-- STEP 5: Create indexes for better performance
-- ==========================================

CREATE INDEX idx_projects_approval_status ON projects(approval_status);
CREATE INDEX idx_projects_submitted_by ON projects(submitted_by_id);
CREATE INDEX idx_projects_user_submitted ON projects(is_user_submitted);
CREATE INDEX idx_projects_transaction_type ON projects(transaction_type);

-- ==========================================
-- STEP 6: Update project_configuration field
-- (This currently stores BHK as string, we'll extract from it or use bedrooms field)
-- ==========================================

-- Optionally, extract BHK from projectConfiguration if needed
-- UPDATE projects 
-- SET bedrooms = CAST(SUBSTRING_INDEX(projectConfiguration, ' ', 1) AS UNSIGNED)
-- WHERE projectConfiguration REGEXP '^[0-9]+ BHK';
```

---

## Updated Project Entity

After running the migration, here's the updated Project.java with new fields:

```java
// Additional fields to add to Project.java

@Column(name = "bedrooms")
private Integer bedrooms;

@Column(name = "bathrooms")
private Integer bathrooms;

@Column(name = "balconies")
private Integer balconies;

@Column(name = "floor_number")
private Integer floorNumber;

@Column(name = "total_floors")
private Integer totalFloors;

@Column(name = "facing")
private String facing;

@Column(name = "age_of_construction")
private Integer ageOfConstruction;

@Column(name = "carpet_area_sqft")
private Double carpetAreaSqft;

@Column(name = "built_up_area_sqft")
private Double builtUpAreaSqft;

@Column(name = "super_built_up_area_sqft")
private Double superBuiltUpAreaSqft;

@Column(name = "plot_area_sqft")
private Double plotAreaSqft;

@Column(name = "price_per_sqft")
private Double pricePerSqft;

@Column(name = "maintenance_charges")
private Double maintenanceCharges;

@Column(name = "booking_amount")
private Double bookingAmount;

@Column(name = "furnished_status")
private String furnishedStatus;

@Column(name = "parking_details")
private String parkingDetails;

@Column(name = "transaction_type")
private String transactionType; // Sale or Rent

@Column(name = "listing_type")
private String listingType; // Residential or Commercial

@Column(name = "property_subtype")
private String propertySubtype; // Apartment, Villa, etc.

@Column(name = "possession_status")
private String possessionStatus;

@Column(name = "occupancy_status")
private String occupancyStatus;

@Column(name = "notice_period")
private Integer noticePeriod;

// Contact Information
@Column(name = "contact_name")
private String contactName;

@Column(name = "contact_phone")
private String contactPhone;

@Column(name = "contact_email")
private String contactEmail;

@Column(name = "preferred_time")
private String preferredTime;

@Lob
@Column(name = "additional_notes")
private String additionalNotes;

// User Submission Tracking
@Enumerated(EnumType.STRING)
@Column(name = "approval_status")
private ProjectApprovalStatus approvalStatus;

@Column(name = "is_user_submitted")
private Boolean isUserSubmitted = false;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "submitted_by_id")
@JsonIgnore
private User submittedBy;

@Column(name = "submitted_at")
private LocalDateTime submittedAt;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "approved_by_id")
@JsonIgnore
private User approvedBy;

@Column(name = "approved_at")
private LocalDateTime approvedAt;

@Lob
@Column(name = "rejection_reason")
private String rejectionReason;
```

---

## Create the Approval Status Enum

**File:** `mpf-backend/src/main/java/com/mypropertyfact/estate/enums/ProjectApprovalStatus.java`

```java
package com.mypropertyfact.estate.enums;

public enum ProjectApprovalStatus {
    DRAFT,              // Saved by user, not yet submitted
    PENDING,            // Submitted for approval
    APPROVED,           // Approved by admin
    REJECTED,           // Rejected by admin
    REQUIRES_CHANGES    // Needs modifications
}
```

---

## Field Mapping Summary

| Form Field | Maps To | Current Status |
|-----------|---------|----------------|
| listingType | listing_type | ❌ Need to add |
| transaction | transaction_type | ❌ Need to add |
| subType | property_subtype | ❌ Need to add |
| title | projectName | ✅ Exists |
| description | locationDesc | ✅ Exists |
| status | possession_status | ❌ Need to add |
| possession | possession_status | ❌ Need to add |
| occupancy | occupancy_status | ❌ Need to add |
| noticePeriod | notice_period | ❌ Need to add |
| projectName | projectName | ✅ Exists |
| builderName | builder (relationship) | ✅ Exists |
| address | locationDesc | ✅ Exists |
| locality | projectLocality | ✅ Exists |
| city | city (relationship) | ✅ Exists |
| carpetArea | carpet_area_sqft | ❌ Need to add |
| builtUpArea | built_up_area_sqft | ❌ Need to add |
| superBuiltUpArea | super_built_up_area_sqft | ❌ Need to add |
| plotArea | plot_area_sqft | ❌ Need to add |
| totalPrice | projectPrice | ✅ Exists |
| pricePerSqFt | price_per_sqft | ❌ Need to add |
| maintenanceCharges | maintenance_charges | ❌ Need to add |
| bookingAmount | booking_amount | ❌ Need to add |
| floor | floor_number | ❌ Need to add |
| totalFloors | total_floors | ❌ Need to add |
| facing | facing | ❌ Need to add |
| ageOfConstruction | age_of_construction | ❌ Need to add |
| bedrooms | bedrooms | ❌ Need to add |
| bathrooms | bathrooms | ❌ Need to add |
| balconies | balconies | ❌ Need to add |
| parking | parking_details | ❌ Need to add |
| furnished | furnished_status | ❌ Need to add |
| amenities | amenities (relationship) | ✅ Exists |
| images | projectGalleries | ✅ Exists |
| contactName | contact_name | ❌ Need to add |
| contactPhone | contact_phone | ❌ Need to add |
| contactEmail | contact_email | ❌ Need to add |

---

## Recommendation

**✅ Use the existing Project table by adding columns.**

Why?
1. Maintains data consistency
2. Reuses existing relationships
3. Avoids duplicate data
4. Easier to query all projects together
5. Uses existing gallery, amenity, and floor plan tables

**❌ Don't create a new entity** - it would cause data duplication and complexity.

---

## Next Steps

1. Run the SQL migration script above
2. Update Project.java entity
3. Create ProjectApprovalStatus enum
4. Update ProjectRepository with new query methods
5. Create backend services for submission

Would you like me to create the complete updated Project entity file?


