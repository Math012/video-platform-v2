import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageUserVideosComponent } from './manage-user-videos.component';

describe('ManageUserVideosComponent', () => {
  let component: ManageUserVideosComponent;
  let fixture: ComponentFixture<ManageUserVideosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageUserVideosComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ManageUserVideosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
